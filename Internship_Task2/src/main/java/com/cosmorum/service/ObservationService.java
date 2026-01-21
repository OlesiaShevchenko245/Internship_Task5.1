package com.cosmorum.service;

import com.cosmorum.dto.*;
import com.cosmorum.entity.AstronomicalObservation;
import com.cosmorum.entity.Author;
import com.cosmorum.exception.ResourceNotFoundException;
import com.cosmorum.repository.AstronomicalObservationRepository;
import com.cosmorum.repository.AuthorRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObservationService {

    private final AstronomicalObservationRepository observationRepository;
    private final AuthorRepository authorRepository;
    private final EmailMbPublisher emailMbPublisher;

    @org.springframework.beans.factory.annotation.Value("${admin.email}")
    private String adminEmail;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ObservationService(
            AstronomicalObservationRepository observationRepository,
            AuthorRepository authorRepository,
            EmailMbPublisher emailMbPublisher) {
        this.observationRepository = observationRepository;
        this.authorRepository = authorRepository;
        this.emailMbPublisher = emailMbPublisher;
    }

    @Transactional
    public ObservationDTO create(ObservationDTO dto) {
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + dto.getAuthorId()));

        AstronomicalObservation observation = new AstronomicalObservation();
        observation.setName(dto.getName());
        observation.setDescription(dto.getDescription());
        observation.setObservationTime(dto.getObservationTime());
        observation.setAuthor(author);
        observation.setCelestialObjects(dto.getCelestialObjects());

        AstronomicalObservation saved = observationRepository.save(observation);

        // changes for email
        EmailSendRequest email = new EmailSendRequest();
        email.setEventId(java.util.UUID.randomUUID().toString());
        email.setSourceService("task2-observations-service");
        email.setTemplate("OBSERVATION_CREATED");
        email.setSubject("New observation created");
        email.setContent("Observation created with id=" + saved.getId() + ", name=" + saved.getName());
        email.setCreatedAt(java.time.Instant.now());

        email.setRecipients(java.util.List.of(adminEmail));

        emailMbPublisher.publish(email);

        return toFullDTO(saved);
    }

    @Transactional(readOnly = true)
    public ObservationDTO getById(Long id) {
        AstronomicalObservation observation = observationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observation not found with id: " + id));
        return toFullDTO(observation);
    }

    @Transactional
    public ObservationDTO update(Long id, ObservationDTO dto) {
        AstronomicalObservation observation = observationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observation not found with id: " + id));

        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + dto.getAuthorId()));

        observation.setName(dto.getName());
        observation.setDescription(dto.getDescription());
        observation.setObservationTime(dto.getObservationTime());
        observation.setAuthor(author);
        observation.setCelestialObjects(dto.getCelestialObjects());

        return toFullDTO(observationRepository.save(observation));
    }

    @Transactional
    public void delete(Long id) {
        if (!observationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Observation not found with id: " + id);
        }
        observationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ObservationListResponse list(ObservationFilterRequest request) {

        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize());

        String name = request.getName();
        LocalDateTime startTime = request.getStartTime();

        Page<AstronomicalObservation> page;

        if (name != null && !name.isBlank() && startTime != null) {
            page = observationRepository.findWithNameAndStartTime(
                    request.getAuthorId(),
                    name,
                    startTime,
                    pageable);
        } else if (name != null && !name.isBlank()) {
            page = observationRepository.findWithName(
                    request.getAuthorId(),
                    name,
                    pageable);
        } else if (startTime != null) {
            page = observationRepository.findWithStartTime(
                    request.getAuthorId(),
                    startTime,
                    pageable);
        } else {
            page = observationRepository.findBase(
                    request.getAuthorId(),
                    pageable);
        }

        List<ObservationListDTO> list = page.getContent()
                .stream()
                .map(this::toListDTO)
                .collect(Collectors.toList());

        return new ObservationListResponse(list, page.getTotalPages());
    }

    @Transactional(readOnly = true)
    public byte[] generateReport(ObservationFilterRequest request) throws IOException {

        String name = request.getName();
        LocalDateTime startTime = request.getStartTime();

        List<AstronomicalObservation> observations;

        if (name != null && !name.isBlank() && startTime != null) {
            observations = observationRepository.findWithNameAndStartTime(
                    request.getAuthorId(), name, startTime, Pageable.unpaged()).getContent();
        } else if (name != null && !name.isBlank()) {
            observations = observationRepository.findWithName(
                    request.getAuthorId(), name, Pageable.unpaged()).getContent();
        } else if (startTime != null) {
            observations = observationRepository.findWithStartTime(
                    request.getAuthorId(), startTime, Pageable.unpaged()).getContent();
        } else {
            observations = observationRepository.findBase(
                    request.getAuthorId(), Pageable.unpaged()).getContent();
        }

        return createExcelReport(observations);
    }

    @Transactional
    public UploadResponse uploadFromJson(List<ObservationDTO> observations) {

        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        for (ObservationDTO dto : observations) {
            try {
                if (dto.getAuthorId() == null ||
                        !authorRepository.existsById(dto.getAuthorId())) {
                    failureCount++;
                    errors.add("Author not found for observation: " + dto.getName());
                    continue;
                }
                create(dto);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                errors.add("Failed to import: " + dto.getName() + " - " + e.getMessage());
            }
        }

        return new UploadResponse(successCount, failureCount, errors);
    }

    private ObservationDTO toFullDTO(AstronomicalObservation observation) {
        ObservationDTO dto = new ObservationDTO();
        dto.setId(observation.getId());
        dto.setName(observation.getName());
        dto.setDescription(observation.getDescription());
        dto.setObservationTime(observation.getObservationTime());
        dto.setAuthorId(observation.getAuthor().getId());
        dto.setCelestialObjects(observation.getCelestialObjects());

        dto.setAuthor(new AuthorDTO(
                observation.getAuthor().getId(),
                observation.getAuthor().getFirstName(),
                observation.getAuthor().getLastName(),
                observation.getAuthor().getNationality()));

        return dto;
    }

    private ObservationListDTO toListDTO(AstronomicalObservation observation) {
        return new ObservationListDTO(
                observation.getId(),
                observation.getName(),
                observation.getObservationTime(),
                observation.getAuthor().getFirstName() + " " +
                        observation.getAuthor().getLastName());
    }

    private byte[] createExcelReport(List<AstronomicalObservation> observations) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Astronomical Observations");

            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "ID", "Name", "Description",
                    "Observation Time", "Author", "Celestial Objects"
            };

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (AstronomicalObservation obs : observations) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(obs.getId());
                row.createCell(1).setCellValue(obs.getName());
                row.createCell(2).setCellValue(obs.getDescription());
                row.createCell(3).setCellValue(obs.getObservationTime().format(FORMATTER));
                row.createCell(4).setCellValue(
                        obs.getAuthor().getFirstName() + " " + obs.getAuthor().getLastName());
                row.createCell(5).setCellValue(
                        obs.getCelestialObjects() != null
                                ? String.join(", ", obs.getCelestialObjects())
                                : "");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
package com.cosmorum.controller;

import com.cosmorum.dto.*;
import com.cosmorum.service.ObservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/observation")
public class ObservationController {
    
    private final ObservationService observationService;
    
    public ObservationController(ObservationService observationService) {
        this.observationService = observationService;
    }
    
    @PostMapping
    public ResponseEntity<ObservationDTO> create(@Valid @RequestBody ObservationDTO observationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(observationService.create(observationDTO));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ObservationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(observationService.getById(id));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ObservationDTO> update(@PathVariable Long id, @Valid @RequestBody ObservationDTO observationDTO) {
        return ResponseEntity.ok(observationService.update(id, observationDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        observationService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/_list")
    public ResponseEntity<ObservationListResponse> list(@RequestBody ObservationFilterRequest request) {
        return ResponseEntity.ok(observationService.list(request));
    }
    
    @PostMapping("/_report")
    public ResponseEntity<byte[]> report(@RequestBody ObservationFilterRequest request) throws IOException {
        byte[] excelData = observationService.generateReport(request);
        
        String filename = "astronomical_observations_" + 
                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                         ".xlsx";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
    
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(@RequestBody List<ObservationDTO> observations) {
        return ResponseEntity.ok(observationService.uploadFromJson(observations));
    }
}
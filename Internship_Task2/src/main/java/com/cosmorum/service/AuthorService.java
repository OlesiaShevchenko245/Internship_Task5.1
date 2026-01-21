package com.cosmorum.service;

import com.cosmorum.dto.AuthorDTO;
import com.cosmorum.entity.Author;
import com.cosmorum.exception.DuplicateResourceException;
import com.cosmorum.exception.ResourceNotFoundException;
import com.cosmorum.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    public AuthorDTO create(AuthorDTO dto) {
        if (authorRepository.existsByNationality(dto.getNationality())) {
            throw new DuplicateResourceException(
                    "Author with nationality '" + dto.getNationality() + "' already exists"
            );
        }

        Author author = new Author();
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        author.setNationality(dto.getNationality());

        Author saved = authorRepository.save(author);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<AuthorDTO> getAll() {
        return authorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AuthorDTO getById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
        return toDTO(author);
    }

    @Transactional
    public AuthorDTO update(Long id, AuthorDTO dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));

        if (!author.getNationality().equals(dto.getNationality()) &&
                authorRepository.existsByNationality(dto.getNationality())) {
            throw new DuplicateResourceException(
                    "Author with nationality '" + dto.getNationality() + "' already exists"
            );
        }

        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        author.setNationality(dto.getNationality());

        Author updated = authorRepository.save(author);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }

    private AuthorDTO toDTO(Author author) {
        return new AuthorDTO(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getNationality()
        );
    }
}

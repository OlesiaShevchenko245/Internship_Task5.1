package com.cosmorum.repository;

import com.cosmorum.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNationality(String nationality);
    boolean existsByNationality(String nationality);
}

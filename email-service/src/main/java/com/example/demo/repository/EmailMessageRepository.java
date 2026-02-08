package com.example.demo.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.model.EmailMessageDoc;
import com.example.demo.model.EmailStatus;

public interface EmailMessageRepository extends ElasticsearchRepository<EmailMessageDoc, String> {
    List<EmailMessageDoc> findTop100ByStatusOrderByLastAttemptAtAsc(EmailStatus status);
}

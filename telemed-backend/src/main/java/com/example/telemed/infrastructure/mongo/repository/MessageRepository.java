package com.example.telemed.infrastructure.mongo.repository;

import com.example.telemed.domain.model.Role;
import com.example.telemed.infrastructure.mongo.document.MessageDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<MessageDocument, String> {
  List<MessageDocument> findByConsultationIdAndSequenceGreaterThanOrderBySequenceAsc(
      String consultationId, long afterSequence, Pageable pageable);
  List<MessageDocument> findByConsultationIdAndAuthorRoleAndSequenceGreaterThanOrderBySequenceAsc(
      String consultationId, Role role, long afterSequence, Pageable pageable);
}
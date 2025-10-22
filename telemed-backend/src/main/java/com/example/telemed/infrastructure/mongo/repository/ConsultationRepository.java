package com.example.telemed.infrastructure.mongo.repository;

import com.example.telemed.infrastructure.mongo.document.ConsultationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConsultationRepository extends MongoRepository<ConsultationDocument, String> {
    List<ConsultationDocument> findByDoctorIdOrderByCreatedAtDesc(String doctorId);
    List<ConsultationDocument> findByPatientIdOrderByCreatedAtDesc(String patientId);
}
package com.example.telemed.infrastructure.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document("consultations")
public class ConsultationDocument {
  @Id public String id;
  public String patientId;
  public String doctorId;
  public Instant createdAt;
}
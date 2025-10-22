package com.example.telemed.infrastructure.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("idempotency")
public class IdempotencyRecord {
  @Id public String id; // Idempotency-Key
  public String requestHash;
  public String state; // PENDING | COMPLETED | FAILED
  public Integer statusCode;
  public String responseBody; // cached JSON
  public Instant createdAt;
}
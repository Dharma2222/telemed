package com.example.telemed.domain.model;

import java.time.Instant;
import java.util.Objects;

public final class Consultation {
  private final String id;
  private final String patientId;
  private final String doctorId;
  private final Instant createdAt;

  public Consultation(String id, String patientId, String doctorId, Instant createdAt) {
    this.id = id;
    this.patientId = Objects.requireNonNull(patientId);
    this.doctorId  = Objects.requireNonNull(doctorId);
    this.createdAt = createdAt == null ? Instant.now() : createdAt;
  }
  public String id(){ return id; }
  public String patientId(){ return patientId; }
  public String doctorId(){ return doctorId; }
  public Instant createdAt(){ return createdAt; }
}
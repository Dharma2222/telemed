package com.example.telemed.application;

public interface SequenceGenerator {
  long nextSequenceForConsultation(String consultationId);
}
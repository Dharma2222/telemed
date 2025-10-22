package com.example.telemed.api.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateConsultationRequest {
  @NotBlank public String patientId;
  @NotBlank public String doctorId;
}
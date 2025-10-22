package com.example.telemed.api.controller;

import com.example.telemed.api.dto.CreateConsultationRequest;
import com.example.telemed.infrastructure.mongo.document.ConsultationDocument;
import com.example.telemed.infrastructure.mongo.repository.ConsultationRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/consultations")
public class ConsultationController {
  private final ConsultationRepository repo;
  public ConsultationController(ConsultationRepository repo){ this.repo = repo; }

  @PostMapping
  public ConsultationDocument create(@Valid @RequestBody CreateConsultationRequest req){
    var c = new ConsultationDocument();
    c.patientId = req.patientId;
    c.doctorId = req.doctorId;
    c.createdAt = Instant.now();
    return repo.save(c);
  }

  @GetMapping("/{id}")
  public ConsultationDocument get(@PathVariable String id){
    return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Consultation not found"));
  }
  @GetMapping
  public List<ConsultationDocument> listByParticipant(
          @RequestParam(value = "doctorId", required = false) String doctorId,
          @RequestParam(value = "patientId", required = false) String patientId) {

    if ((doctorId == null) == (patientId == null)) {
      throw new IllegalArgumentException("Provide exactly one of doctorId or patientId");
    }
    return doctorId != null
            ? repo.findByDoctorIdOrderByCreatedAtDesc(doctorId)
            : repo.findByPatientIdOrderByCreatedAtDesc(patientId);
  }
}

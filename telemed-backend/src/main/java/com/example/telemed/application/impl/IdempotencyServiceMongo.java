package com.example.telemed.application.impl;

import com.example.telemed.application.IdempotencyService;
import com.example.telemed.infrastructure.mongo.document.IdempotencyRecord;
import com.example.telemed.infrastructure.mongo.repository.IdempotencyRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class IdempotencyServiceMongo implements IdempotencyService {
  private final IdempotencyRepository repo;

  public IdempotencyServiceMongo(IdempotencyRepository repo) { this.repo = repo; }

  @Override
  public String hash(String normalizedRequest) {
    return "sha256:" + DigestUtils.sha256Hex(normalizedRequest);
  }

  @Override
  public BeginResult begin(String key, String requestHash) {
    try {
      var rec = new IdempotencyRecord();
      rec.id = key;
      rec.requestHash = requestHash;
      rec.state = "PENDING";
      rec.createdAt = Instant.now();
      repo.insert(rec);
      return new BeginResult(BeginResultType.NEW, null, null);
    } catch (DuplicateKeyException e) {
      var existing = repo.findById(key).orElse(null);
      if (existing == null) return new BeginResult(BeginResultType.IN_PROGRESS, null, null);
      if ("COMPLETED".equals(existing.state)) {
        return new BeginResult(BeginResultType.REPLAY_COMPLETED, existing.statusCode, existing.responseBody);
      }
      if (!requestHash.equals(existing.requestHash)) {
        return new BeginResult(BeginResultType.CONFLICT, null, null);
      }
      return new BeginResult(BeginResultType.IN_PROGRESS, null, null);
    }
  }

  @Override
  public void complete(String key, int statusCode, String responseJson) {
    var rec = repo.findById(key).orElseThrow();
    rec.state = "COMPLETED";
    rec.statusCode = statusCode;
    rec.responseBody = responseJson;
    repo.save(rec);
  }

  @Override
  public void fail(String key) {
    repo.findById(key).ifPresent(r -> { r.state = "FAILED"; repo.save(r); });
  }
}

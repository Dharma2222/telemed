package com.example.telemed.infrastructure.mongo.repository;

import com.example.telemed.infrastructure.mongo.document.IdempotencyRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IdempotencyRepository extends MongoRepository<IdempotencyRecord, String> {}
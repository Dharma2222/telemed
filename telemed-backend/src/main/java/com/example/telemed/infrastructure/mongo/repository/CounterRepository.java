package com.example.telemed.infrastructure.mongo.repository;

import com.example.telemed.infrastructure.mongo.document.CounterDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CounterRepository extends MongoRepository<CounterDocument, String> {}
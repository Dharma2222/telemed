package com.example.telemed.infrastructure.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("counters")
public class CounterDocument {
  @Id public String id;   // "consultation:{consultationId}"
  public long nextSeq;
}
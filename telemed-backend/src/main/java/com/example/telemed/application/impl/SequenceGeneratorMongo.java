package com.example.telemed.application.impl;

import com.example.telemed.application.SequenceGenerator;
import com.example.telemed.infrastructure.mongo.document.CounterDocument;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class SequenceGeneratorMongo implements SequenceGenerator {
  private final MongoTemplate mongo;

  public SequenceGeneratorMongo(MongoTemplate mongo) { this.mongo = mongo; }

  @Override
  public long nextSequenceForConsultation(String consultationId) {
    String id = "consultation:" + consultationId;
    var update = new Update().inc("nextSeq", 1);
    var opts = FindAndModifyOptions.options().upsert(true).returnNew(true);
    var doc = mongo.findAndModify(
        query(where("_id").is(id)), update, opts, CounterDocument.class, "counters");
    return doc.nextSeq;
  }
}

package com.example.telemed.infrastructure.mongo.document;

import com.example.telemed.domain.model.Role;
import com.example.telemed.domain.model.content.MessageContent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("messages")
@CompoundIndex(name="consult_seq_idx", def="{ 'consultationId':1, 'sequence':1 }")
@CompoundIndex(name="consult_role_seq_idx", def="{ 'consultationId':1, 'authorRole':1, 'sequence':1 }")
public class MessageDocument {
  @Id public String id;
  public String consultationId;
  public long sequence;
  public String authorId;
  public Role authorRole;
  public Instant timestamp;
  public MessageContent content;
}
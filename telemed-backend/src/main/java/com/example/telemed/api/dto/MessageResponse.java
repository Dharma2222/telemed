package com.example.telemed.api.dto;

import com.example.telemed.domain.model.Role;
import com.example.telemed.domain.model.content.MessageContent;

import java.time.Instant;

public class MessageResponse {
  public String id;
  public String consultationId;
  public long sequence;
  public String authorId;
  public Role authorRole;
  public Instant timestamp;
  public MessageContent content;
}

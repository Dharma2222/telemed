package com.example.telemed.domain.model;

import com.example.telemed.domain.model.content.MessageContent;
import java.time.Instant;
import java.util.Objects;

// Immutable + Manual Builder
public final class Message {
  private final String id;
  private final String consultationId;
  private final long sequence;
  private final String authorId;
  private final Role authorRole;
  private final Instant timestamp;
  private final MessageContent content;

  private Message(Builder b){
    this.id = b.id;
    this.consultationId = Objects.requireNonNull(b.consultationId);
    if (b.sequence <= 0) throw new IllegalStateException("sequence must be > 0");
    this.sequence = b.sequence;
    this.authorId = Objects.requireNonNull(b.authorId);
    this.authorRole = Objects.requireNonNull(b.authorRole);
    this.timestamp = Objects.requireNonNull(b.timestamp);
    this.content = Objects.requireNonNull(b.content);
  }

  public static class Builder {
    private String id, consultationId, authorId;
    private long sequence;
    private Role authorRole;
    private Instant timestamp;
    private MessageContent content;

    public Builder id(String v){ this.id=v; return this; }
    public Builder consultationId(String v){ this.consultationId=v; return this; }
    public Builder sequence(long v){ this.sequence=v; return this; }
    public Builder authorId(String v){ this.authorId=v; return this; }
    public Builder authorRole(Role v){ this.authorRole=v; return this; }
    public Builder timestamp(Instant v){ this.timestamp=v; return this; }
    public Builder content(MessageContent v){ this.content=v; return this; }
    public Message build(){ return new Message(this); }
  }

  public String id(){ return id; }
  public String consultationId(){ return consultationId; }
  public long sequence(){ return sequence; }
  public String authorId(){ return authorId; }
  public Role authorRole(){ return authorRole; }
  public Instant timestamp(){ return timestamp; }
  public MessageContent content(){ return content; }
}
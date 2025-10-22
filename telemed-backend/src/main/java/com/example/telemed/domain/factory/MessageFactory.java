package com.example.telemed.domain.factory;

import com.example.telemed.domain.model.*;
import com.example.telemed.domain.model.content.*;

import java.time.Instant;

public final class MessageFactory {
  public Message createText(String consultationId, long seq, String authorId, Role role, String text){
    return new Message.Builder()
        .consultationId(consultationId)
        .sequence(seq)
        .authorId(authorId)
        .authorRole(role)
        .timestamp(Instant.now())
        .content(new TextContent(text))
        .build();
  }
  public Message createMedia(String consultationId, long seq, String authorId, Role role,
                             String storageKey, String mimeType, long sizeBytes){
    return new Message.Builder()
        .consultationId(consultationId)
        .sequence(seq)
        .authorId(authorId)
        .authorRole(role)
        .timestamp(Instant.now())
        .content(new MediaContent(storageKey, mimeType, sizeBytes))
        .build();
  }
}
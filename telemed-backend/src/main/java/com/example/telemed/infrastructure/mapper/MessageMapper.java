package com.example.telemed.infrastructure.mapper;

import com.example.telemed.domain.model.Message;
import com.example.telemed.infrastructure.mongo.document.MessageDocument;

public class MessageMapper {
  public static MessageDocument toDocument(Message m){
    var d = new MessageDocument();
    d.consultationId = m.consultationId();
    d.sequence = m.sequence();
    d.authorId = m.authorId();
    d.authorRole = m.authorRole();
    d.timestamp = m.timestamp();
    d.content = m.content();
    return d;
  }
}
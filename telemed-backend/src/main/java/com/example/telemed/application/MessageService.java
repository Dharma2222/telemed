package com.example.telemed.application;

import com.example.telemed.domain.model.Role;
import com.example.telemed.infrastructure.mongo.document.MessageDocument;
import java.util.List;

public interface MessageService {
  MessageDocument createMessageIdempotent(String consultationId,
                                          String idempotencyKey,
                                          String normalizedRequestJson,
                                          String authorId,
                                          Role role,
                                          MessageContentInput content);

  List<MessageDocument> listMessages(String consultationId, Role role, long afterSeq, int limit);
}

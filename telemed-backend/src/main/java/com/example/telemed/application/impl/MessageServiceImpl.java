package com.example.telemed.application.impl;

import com.example.telemed.application.IdempotencyService;
import com.example.telemed.application.MessageContentInput;
import com.example.telemed.application.MessageService;
import com.example.telemed.application.SequenceGenerator;
import com.example.telemed.domain.factory.MessageFactory;
import com.example.telemed.domain.model.Message;
import com.example.telemed.domain.model.Role;
import com.example.telemed.domain.model.content.MediaContent;
import com.example.telemed.domain.model.content.TextContent;
import com.example.telemed.infrastructure.mapper.MessageMapper;
import com.example.telemed.infrastructure.mongo.document.MessageDocument;
import com.example.telemed.infrastructure.mongo.repository.ConsultationRepository;
import com.example.telemed.infrastructure.mongo.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
  private final MessageRepository messageRepo;
  private final ConsultationRepository consultRepo;
  private final SequenceGenerator seq;
  private final IdempotencyService idem;
  private final MessageFactory factory = new MessageFactory();
  private final ObjectMapper om;

  public MessageServiceImpl(MessageRepository messageRepo,
                            ConsultationRepository consultRepo,
                            SequenceGenerator seq,
                            IdempotencyService idem,
                            ObjectMapper om) {
    this.messageRepo = messageRepo;
    this.consultRepo = consultRepo;
    this.seq = seq;
    this.idem = idem;
    this.om = om;
  }

  @Override
  public MessageDocument createMessageIdempotent(String consultationId,
                                                 String idempotencyKey,
                                                 String normalizedRequestJson,
                                                 String authorId,
                                                 Role role,
                                                 MessageContentInput content) {
    var consult = consultRepo.findById(consultationId)
            .orElseThrow(() -> new IllegalArgumentException("Consultation not found"));

    // Enforce: only the consultation's patient/doctor can post, with matching role
    boolean ok = switch (role) {
      case PATIENT -> authorId.equals(consult.patientId);
      case DOCTOR  -> authorId.equals(consult.doctorId);
    };

    if (!ok) {
      throw new IllegalStateException("Author is not a participant in this consultation or role mismatch");
    }

    var begin = idem.begin(idempotencyKey, idem.hash(normalizedRequestJson));
    switch (begin.type()) {
      case REPLAY_COMPLETED -> {
        try {
          return om.readValue(begin.cachedResponse(), MessageDocument.class);
        } catch (Exception e) { throw new RuntimeException(e); }
      }
      case IN_PROGRESS -> throw new IllegalStateException("Request in progress; retry later");
      case CONFLICT -> throw new IllegalStateException("Idempotency-Key used for different request");
      case NEW -> { /* proceed */ }
    }

    try {
      long sequence = seq.nextSequenceForConsultation(consultationId);

      // Build domain message using Factory + manual Builder
      Message domain = switch (content.type) {
        case "TEXT" -> {
          if (content.text == null || content.text.isBlank())
            throw new IllegalArgumentException("text is required for TEXT content");
          yield factory.createText(consultationId, sequence, authorId, role, content.text);
        }
        case "MEDIA" -> {
          if (content.storageKey == null || content.mimeType == null || content.sizeBytes == null)
            throw new IllegalArgumentException("storageKey, mimeType, sizeBytes are required for MEDIA content");
          yield factory.createMedia(consultationId, sequence, authorId, role,
              content.storageKey, content.mimeType, content.sizeBytes);
        }
        default -> throw new IllegalArgumentException("Unsupported content.type");
      };

      // Map to Mongo Document
      MessageDocument saved = messageRepo.save(MessageMapper.toDocument(domain));
      // Serialize and store cached response for idempotent replay
      String json = om.writeValueAsString(saved);
      idem.complete(idempotencyKey, HttpStatus.CREATED.value(), json);
      return saved;
    } catch (RuntimeException ex) {
      idem.fail(idempotencyKey);
      throw ex;
    } catch (Exception ex) {
      idem.fail(idempotencyKey);
      throw new RuntimeException(ex);
    }
  }

  @Override
  public List<MessageDocument> listMessages(String consultationId, Role role, long afterSeq, int limit) {
    var page = PageRequest.of(0, Math.min(limit, 100));
    return role == null
        ? messageRepo.findByConsultationIdAndSequenceGreaterThanOrderBySequenceAsc(consultationId, afterSeq, page)
        : messageRepo.findByConsultationIdAndAuthorRoleAndSequenceGreaterThanOrderBySequenceAsc(consultationId, role, afterSeq, page);
  }
}

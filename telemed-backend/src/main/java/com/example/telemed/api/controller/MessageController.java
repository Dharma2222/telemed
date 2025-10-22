package com.example.telemed.api.controller;

import com.example.telemed.api.dto.CreateMessageRequest;
import com.example.telemed.domain.model.Role;
import com.example.telemed.infrastructure.mongo.document.MessageDocument;
import com.example.telemed.application.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultations/{consultationId}/messages")
public class MessageController {
  private final MessageService service;
  public MessageController(MessageService service){ this.service = service; }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MessageDocument create(@PathVariable String consultationId,
                                @RequestHeader("Idempotency-Key") String idemKey,
                                @Valid @RequestBody CreateMessageRequest req) {

    var input = new com.example.telemed.application.MessageContentInput();
    input.type = req.content.type;
    input.text = req.content.text;
    input.storageKey = req.content.storageKey;
    input.mimeType = req.content.mimeType;
    input.sizeBytes = req.content.sizeBytes;

    return service.createMessageIdempotent(
        consultationId, idemKey, normalize(consultationId, req),
        req.authorId, req.authorRole, input);
  }

  @GetMapping
  public List<MessageDocument> list(@PathVariable String consultationId,
                                    @RequestParam(value="authorRole", required=false) Role role,
                                    @RequestParam(value="cursor", defaultValue="0") long cursor,
                                    @RequestParam(value="limit", defaultValue="50") int limit){
    return service.listMessages(consultationId, role, cursor, limit);
  }

  private String normalize(String consultationId, CreateMessageRequest req){
    var c = req.content;
    return consultationId+"|"+req.authorId+"|"+req.authorRole+"|"+(c.type==null?"":c.type)+"|"
        +(c.text==null?"":c.text)+"|"+(c.storageKey==null?"":c.storageKey)+"|"
        +(c.mimeType==null?"":c.mimeType)+"|"+(c.sizeBytes==null?0:c.sizeBytes);
  }
}

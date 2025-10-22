package com.example.telemed.api.dto;

import com.example.telemed.domain.model.Role;
import jakarta.validation.constraints.*;

public class CreateMessageRequest {
  @NotBlank public String authorId;
  @NotNull public Role authorRole;

  @NotNull public Content content;

  public static class Content {
    @NotBlank public String type; // "TEXT" | "MEDIA"
    public String text;           // when TEXT
    public String storageKey;     // when MEDIA
    public String mimeType;       // when MEDIA
    public Long sizeBytes;        // when MEDIA
  }
}

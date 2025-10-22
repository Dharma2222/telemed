// src/main/java/com/example/telemed/domain/model/content/MediaContent.java
package com.example.telemed.domain.model.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class MediaContent implements MessageContent {
  private final String storageKey; // e.g. local:/...
  private final String mimeType;
  private final long sizeBytes;

  public MediaContent(String storageKey, String mimeType, long sizeBytes) {
    this.storageKey = Objects.requireNonNull(storageKey);
    this.mimeType   = Objects.requireNonNull(mimeType);
    this.sizeBytes  = sizeBytes;
  }

  @JsonProperty("storageKey")
  public String getStorageKey() { return storageKey; }

  @JsonProperty("mimeType")
  public String getMimeType() { return mimeType; }

  @JsonProperty("sizeBytes")
  public long getSizeBytes() { return sizeBytes; }

  @Override
  @JsonProperty("type")
  public String getType() { return "MEDIA"; }
}

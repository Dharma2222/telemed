// src/main/java/com/example/telemed/domain/model/content/TextContent.java
package com.example.telemed.domain.model.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public final class TextContent implements MessageContent {
  private final String text;

  public TextContent(String text) { this.text = Objects.requireNonNull(text).trim(); }

  @JsonProperty("text")
  public String getText() { return text; }

  @Override
  @JsonProperty("type")
  public String getType() { return "TEXT"; }
}

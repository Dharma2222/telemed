// src/main/java/com/example/telemed/domain/model/content/MessageContent.java
package com.example.telemed.domain.model.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"   // ensures {"type":"TEXT"} or {"type":"MEDIA"} appears on the wire
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextContent.class, name = "TEXT"),
        @JsonSubTypes.Type(value = MediaContent.class, name = "MEDIA")
})
public sealed interface MessageContent permits TextContent, MediaContent {
  String getType();
}

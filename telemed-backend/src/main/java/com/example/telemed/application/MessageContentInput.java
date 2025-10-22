package com.example.telemed.application;

public class MessageContentInput {
  public String type;       // "TEXT" | "MEDIA"
  public String text;       // when TEXT
  public String storageKey; // when MEDIA
  public String mimeType;   // when MEDIA
  public Long   sizeBytes;  // when MEDIA
}
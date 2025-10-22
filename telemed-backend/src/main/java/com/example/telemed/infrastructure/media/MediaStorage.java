package com.example.telemed.infrastructure.media;

import java.io.InputStream;

public interface MediaStorage {
  record Stored(String storageKey, String mimeType, long sizeBytes) {}
  Stored store(String filename, String mimeType, long sizeBytes, InputStream data);
}

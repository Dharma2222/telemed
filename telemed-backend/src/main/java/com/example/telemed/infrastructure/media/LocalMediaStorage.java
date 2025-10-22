package com.example.telemed.infrastructure.media;

import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Component
public class LocalMediaStorage implements MediaStorage {
  private final Path root = Paths.get("./media");

  public LocalMediaStorage() {
    try { Files.createDirectories(root); } catch (IOException ignored) {}
  }

  @Override
  public Stored store(String filename, String mimeType, long sizeBytes, InputStream data){
    String key = UUID.randomUUID() + "_" + (filename == null ? "file" : filename);
    Path out = root.resolve(key);
    try (OutputStream os = Files.newOutputStream(out, StandardOpenOption.CREATE_NEW)) {
      data.transferTo(os);
    } catch(IOException e){ throw new UncheckedIOException(e); }
    return new Stored("local:" + out.toAbsolutePath(), mimeType, sizeBytes);
  }
}

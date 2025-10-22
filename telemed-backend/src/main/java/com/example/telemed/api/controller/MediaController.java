package com.example.telemed.api.controller;

import com.example.telemed.api.dto.MediaUploadResponse;
import com.example.telemed.infrastructure.media.MediaStorage;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/media")
public class MediaController {
  private final MediaStorage storage;
  public MediaController(MediaStorage storage){ this.storage = storage; }

  @PostMapping(value="/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public MediaUploadResponse upload(@RequestPart("file") MultipartFile file) throws Exception {
    try (InputStream in = file.getInputStream()){
      var stored = storage.store(file.getOriginalFilename(), file.getContentType(), file.getSize(), in);
      var resp = new MediaUploadResponse();
      resp.storageKey = stored.storageKey();
      resp.message = "Use this storageKey in a MEDIA message.";
      return resp;
    }
  }
}

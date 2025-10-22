package com.example.telemed.application;

public interface IdempotencyService {
  enum BeginResultType { NEW, REPLAY_COMPLETED, IN_PROGRESS, CONFLICT }
  record BeginResult(BeginResultType type, Integer statusCode, String cachedResponse){}

  String hash(String normalizedRequest);
  BeginResult begin(String key, String requestHash);
  void complete(String key, int statusCode, String responseJson);
  void fail(String key);
}
package com.example.telemed.common;

import java.time.Instant;
import java.util.List;

public class ApiError {
  public Instant timestamp = Instant.now();
  public int status;
  public String message;
  public List<String> details;
}

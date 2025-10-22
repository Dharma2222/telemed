package com.example.telemed.common;
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String msg) { super(msg); }
}
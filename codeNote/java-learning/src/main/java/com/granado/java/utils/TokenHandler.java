package com.granado.java.utils;

@FunctionalInterface
public interface TokenHandler {
  String handleToken(String content);
}

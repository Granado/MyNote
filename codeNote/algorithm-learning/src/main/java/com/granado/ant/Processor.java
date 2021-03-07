package com.granado.ant;

@FunctionalInterface
public interface Processor {

  void process(Quota quota);
}

package com.granado.java.reactor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.IntStream;

public class ReactorCourse {

  public static void main(String[] args) {

    CompletableFuture<Void> subTask1;
    CompletableFuture<Void> subTask2;

    try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>()) {

      subTask1 = publisher.consume(System.out::println);
      subTask2 = publisher.consume(System.out::print);
      IntStream.rangeClosed(1, 3).forEach(publisher::submit);
    }

    subTask1.join();
    subTask2.join();
  }
}

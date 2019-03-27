package com.granado.ant;

import java.util.concurrent.LinkedBlockingQueue;

public class QueueProcessor extends Thread implements Processor {

  private LinkedBlockingQueue<Quota> queue = new LinkedBlockingQueue(10000);

  private FileSorter fileSorter;

  public QueueProcessor(FileSorter fileSorter) {
    this.fileSorter = fileSorter;
  }

  @Override
  public void process(Quota quota) {
      try {
        queue.put(quota);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
  }

  @Override
  public void run() {
    super.run();
  }
}

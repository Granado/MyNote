package com.granado.ant;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class FileLoader {

  public static final int MAX_FILE_LOADER = 10;

  private static final int KEEP_LIVE = 1;

  private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

  private LinkedBlockingQueue<Runnable> fileTask = new LinkedBlockingQueue<>();

  private ThreadPoolExecutor fileLoadThreads;

  private FileDiscover fileDiscover;

  private String fileDirectory;

  private File directory;

  private Processor processor;

  private TreeSet<String> fileCache = new TreeSet<>();

  public FileLoader(String fileDirectory, Processor processor) {
    this.fileDirectory = fileDirectory;
    directory = new File(fileDirectory);
    if (!directory.isDirectory()) {
      throw new IllegalArgumentException("need a path of file directory");
    }

    this.processor = processor;

    fileDiscover = new FileDiscover(directory);
  }

  public synchronized void start() {

    if (fileLoadThreads != null && fileLoadThreads.isTerminating()) {
      fileLoadThreads.shutdownNow();
    }

    if (fileLoadThreads == null || fileLoadThreads.isShutdown() || fileLoadThreads.isTerminated()) {
      fileLoadThreads = new ThreadPoolExecutor(MAX_FILE_LOADER, MAX_FILE_LOADER, KEEP_LIVE, TIME_UNIT, fileTask);
    }

    if (fileDiscover.isInterrupted() || !fileDiscover.isAlive()) {
      fileDiscover.start();
    }
  }

  public synchronized void stop() {

    if (fileDiscover.isAlive() || !fileDiscover.isInterrupted()) {
      fileDiscover.interrupt();
    }

    if (fileLoadThreads != null && !(fileLoadThreads.isShutdown()
        || fileLoadThreads.isTerminated()
        || fileLoadThreads.isTerminating())) {
      fileLoadThreads.shutdown();
    }
  }

  // single thread scan new file
  private class FileDiscover extends Thread {

    private File directory;

    public FileDiscover(File directory) {
      this.directory = directory;
    }

    @Override
    public void run() {

      while (!isInterrupted()) {

        File[] files = this.directory
                           .listFiles((dir, name) -> fileDirectory.equals(dir.getAbsolutePath()) && !fileCache.contains(name));

        for (int i = 0; i < files.length; i++) {
          fileCache.add(files[i].getName());
          fileLoadThreads.execute(new FileLoadWork(files[i]));
        }

        LockSupport.parkNanos(this, 1000000);
      }
    }
  }

  private class FileLoadWork implements Runnable {

    private File file;

    public FileLoadWork(File file) {
      this.file = file;
    }

    private Quota parse(String line) {

      if (line == null || line.length() == 0) {
        return null;
      }

      String[] parts = line.split(",");
      if (parts == null || parts.length != 3) {
        return null;
      }

      return new Quota(parts[0], parts[1], Float.valueOf(parts[2]));
    }

    @Override
    public void run() {

      if (file.isDirectory()) {
        return;
      }

      try(RandomAccessFile raf = new RandomAccessFile(file, "r")){
        String line;
        do {
          line = raf.readLine();
          if (line != null) {
            Quota quotaEntity = parse(line);
            processor.process(quotaEntity);
          }
        } while (line != null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    FileLoader loader = new FileLoader("/Users/GranadoYang/Desktop/test", null);
    loader.start();
    loader.fileDiscover.join();
  }
}

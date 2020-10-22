package com.granado.java.thread;

public class RingBuffer {

    private final static int bufferSize = 62;
    private volatile String[] buffer = new String[bufferSize];
    private volatile int head = 0;
    private volatile int tail = 0;

    private Boolean empty() {
        return head == tail;
    }

    private Boolean full() {
        return (tail + 1) % bufferSize == head;
    }

    public Boolean put(String v) {
        if (full()) {
            return false;
        }
        buffer[tail] = v;
        tail = (tail + 1) % bufferSize;
        return true;
    }

    public String get() {
        if (empty()) {
            return null;
        }
        String result = buffer[head];
        head = (head + 1) % bufferSize;
        return result;
    }

    public String[] getAll() {
        if (empty()) {
            return new String[0];
        }
        int copyTail = tail;
        int cnt = head < copyTail ? copyTail - head : bufferSize - head + copyTail;
        String[] result = new String[cnt];
        if (head < copyTail) {
            for (int i = head; i < copyTail; i++) {
                result[i - head] = buffer[i];
            }
        } else {
            for (int i = head; i < bufferSize; i++) {
                result[i - head] = buffer[i];
            }
            for (int i = 0; i < copyTail; i++) {
                result[bufferSize - head + i] = buffer[i];
            }
        }
        head = copyTail;
        return result;
    }

    public static void main(String[] args) {
        RingBuffer ringBuffer = new RingBuffer();
        Thread putter = new Thread(() -> {
            Long counter = 0L;
            while (!Thread.interrupted()) {
                if (!ringBuffer.put(String.valueOf(counter ++))) {
                    System.out.println("full");
                }
            }
        });

        Thread getter = new Thread(() -> {
            while (!Thread.interrupted()) {
                if (!ringBuffer.empty()) {
                    System.out.println(ringBuffer.get());
                } else {
                    System.out.println("empty");
                }
            }
        });

        putter.start();
        getter.start();
        while (true);
    }
}

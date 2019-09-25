package com.granado.java.cap;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Operator implements Runnable {

    protected static AtomicInteger ID_GENERATOR = new AtomicInteger(Integer.valueOf(0));

    protected int id = ID_GENERATOR.getAndIncrement();
}

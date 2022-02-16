package com.centram.batch;

import java.util.concurrent.TimeUnit;

public class Task implements Runnable {
    private final String name;

    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void run() {
        try {
            Long duration = (long) (Math.random() * 10);
            System.out.println("Thread Name : " + Thread.currentThread().getName() + ". Executing : " + name + " for " + duration + " seconds!");
            TimeUnit.SECONDS.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

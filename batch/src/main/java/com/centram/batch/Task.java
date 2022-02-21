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
            TimeUnit.SECONDS.sleep(duration);
            System.out.println("Thread Name : ".concat(Thread.currentThread().getName()).concat(". Executed : ").concat(name).concat(" for ").concat(String.valueOf(duration)).concat(" seconds!"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

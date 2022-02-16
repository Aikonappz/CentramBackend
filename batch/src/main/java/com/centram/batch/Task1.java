package com.centram.batch;

import java.util.Random;
import java.util.concurrent.Callable;

public class Task1 implements Callable {
    public Object call() throws Exception {
        Random generator = new Random();
        Integer randomNumber = generator.nextInt(20);
        Thread.sleep(randomNumber * 1000);
        return randomNumber;
    }
}
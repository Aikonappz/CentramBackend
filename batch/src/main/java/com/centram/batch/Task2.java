package com.centram.batch;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Task2 implements Callable<Integer> {

    private final Integer number;

    public Task2(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    public Integer call() throws Exception {
        int result = 1;
        if ((number == 0) || (number == 1)) {
            result = 1;
        } else {
            for (int i = 2; i <= number; i++) {
                result *= i;
                TimeUnit.MILLISECONDS.sleep(20);
            }
        }
        //System.out.println("Result for number - ".concat(String.valueOf(number)).concat(" -> ").concat(String.valueOf(result)));
        return result;
    }
}

package com.yefeiw;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        // write your code here

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        TestRunnable testRunnable = new TestRunnable();

        executor.scheduleAtFixedRate(testRunnable,
                1,
                4,
                TimeUnit.MINUTES);


    }
}



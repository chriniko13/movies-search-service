package com.chriniko.lunatech.movies.config;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.logging.Level;

@Configuration
public class BasicConfig {

    @Bean(destroyMethod = "shutdown", autowire = Autowire.BY_NAME)
    public ThreadPoolExecutor workers() {

        int processors = Runtime.getRuntime().availableProcessors();

        ThreadFactory defaultFactory = Executors.defaultThreadFactory();

        return new ThreadPoolExecutor(
                processors,
                2 * processors,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(50),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = defaultFactory.newThread(r);
                        thread.setUncaughtExceptionHandler(new DefaultExceptionHandler());
                        return thread;
                    }
                },
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Log
    static class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable t) {
            log.log(Level.SEVERE,
                    "[threadName: " + thread.getName() + "] message: " + t.getMessage() + ", stackTrace: " + Arrays.toString(t.getStackTrace()));
        }
    }

}

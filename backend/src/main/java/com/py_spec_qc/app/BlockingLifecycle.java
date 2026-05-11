package com.py_spec_qc.app;

import java.util.concurrent.CountDownLatch;
import org.springframework.stereotype.Component;

@Component
public final class BlockingLifecycle {
    private final CountDownLatch latch = new CountDownLatch(1);

    public BlockingLifecycle() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> latch.countDown()));
    }

    public void block() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


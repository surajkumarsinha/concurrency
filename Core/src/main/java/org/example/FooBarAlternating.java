package org.example;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class FooBarAlternating {
	public static void main(String[] args) {
		Random random = new Random();
		Semaphore foo = new Semaphore(1);
		Semaphore bar = new Semaphore(0);

		try(ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
			executor.submit(() -> {
				while(Thread.currentThread().isAlive()) {
					try {
						foo.acquire();
						Thread.sleep(random.nextLong(1000, 5000));
						System.out.println("foo");
						bar.release();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			});
			executor.submit(() -> {
				while(Thread.currentThread().isAlive()) {
					try {
						bar.acquire();
						Thread.sleep(random.nextLong(1000, 5000));
						System.out.println("bar");
						foo.release();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			});
		}
	}
}

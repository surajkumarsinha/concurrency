package org.example;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class PrintInOrder {

	public static void main(String[] args) {

		Random random = new Random();

		try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

			runViaCompletableFutures(random, executor);
			runViaSemaphores(executor, random);
		}
	}

	private static void runViaSemaphores(ExecutorService executor, Random random) {
		Semaphore semSecond = new Semaphore(0);
		Semaphore semThird = new Semaphore(0);
		executor.submit(() -> {
			sleep(random);
			System.out.println("First thread");
			semSecond.release();
		});

		executor.submit(() -> {
			try {
				semSecond.acquire();
				sleep(random);
				System.out.println("Second thread");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			semThird.release();
		});

		executor.submit(() -> {
			try {
				semThird.acquire();
				sleep(random);
				System.out.println("Third thread");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
	}

	private static void runViaCompletableFutures(Random random, ExecutorService executor) {
		CompletableFuture
			.runAsync(() -> {
				sleep(random);
				System.out.println("First thread");
			}, executor)
			.thenRunAsync(() -> {
				sleep(random);
				System.out.println("Second thread");
			}, executor)

			.thenRunAsync(() -> {
				sleep(random);
				System.out.println("Third thread");
			}, executor)

						.join();
	}

	static void sleep(Random r) {
		try {
			Thread.sleep(r.nextLong(1000, 5000));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
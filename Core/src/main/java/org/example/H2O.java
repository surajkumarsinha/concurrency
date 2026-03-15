package org.example;

import java.util.Random;
import java.util.concurrent.*;

public class H2O {

//	private static final CyclicBarrier barrier = new CyclicBarrier(3);
//	private static final Semaphore oxy = new Semaphore(0);
	private static final Semaphore hyd = new Semaphore(2);
	private static final Semaphore hydDone = new Semaphore(0);

	private static int hydCount = 0;

	public static void main(String[] args) throws InterruptedException {
		Random random = new Random();
		Thread.ofVirtual().start(() -> {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					Thread.ofVirtual().start(new Hydrogen());
					Thread.sleep(random.nextLong(500, 3000));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		Thread.ofVirtual().start(() -> {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					Thread.ofVirtual().start(new Oxygen());
					Thread.sleep(random.nextLong(500, 3000));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		Thread.sleep(20000);
	}

	static class Hydrogen implements Runnable {
		@Override
		public void run() {
			try {
				hyd.acquire();
				System.out.println("H");
				hydDone.release();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	static class Oxygen implements Runnable {
		@Override
		public void run() {
			try {
				hydDone.acquire(2);
				System.out.println("O");
				System.out.println("");
				hyd.release(2);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}

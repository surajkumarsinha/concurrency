package org.example.classic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class PhilosophersDining {

	private static final Semaphore[] forks = new Semaphore[5];
	private static final Philosopher[] philosophers = new Philosopher[5];
	private static final Semaphore footman = new Semaphore(4);
	public static void main(String[] args) throws InterruptedException {
		for(int i=0; i<5; i++) {
			forks[i] = new Semaphore(1);
			philosophers[i] = new Philosopher(i);
		}
		ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
		for(int i=0; i<5; i++) executorService.submit(philosophers[i]);
		Thread.sleep(20000);
	}

	static class Philosopher implements Runnable {
		private final int id;

		public Philosopher(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			try {
				while(!Thread.currentThread().isInterrupted()) {
					System.out.println("Philosopher: " + id + " is hungry");
					getForks();
					eat();
					putForks();
					think();
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		private void getForks() throws InterruptedException {
			footman.acquire();
			getLeft(id).acquire();
			getRight(id).acquire();
		}

		private void putForks() {
			getLeft(id).release();
			getRight(id).release();
			footman.release();
		}

		private void eat() throws InterruptedException {
			System.out.println("Philosopher: " + id + " is eating");
			Thread.sleep(1000);
		}
		private void think() throws InterruptedException {
			System.out.println("Philosopher: " + id + " is thinking");
			Thread.sleep(2000);
		}
	}

	static Semaphore getLeft(int i) {
		return forks[i];
	}

	static Semaphore getRight(int i) {
		return forks[(i+1)%5];
	}
}

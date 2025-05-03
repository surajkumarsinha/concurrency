package org.example.water;


import java.util.concurrent.Semaphore;

public class SolutionA {
	private static Semaphore hyd = new Semaphore(0); // signal that 2 hydrogens are available now
	private static Semaphore hydMutex = new Semaphore(1); // mutex to allow 2 hydrogens
	private static Semaphore oxy = new Semaphore(0);
	private static Semaphore mutex = new Semaphore(1);
	private static Semaphore barrier = new Semaphore(3);
	private static int hydCount = 0;

	public static void main(String[] args) throws InterruptedException {
		Thread.ofVirtual().start(() -> {
			int i=1;
			while(!Thread.currentThread().isInterrupted()) {
				Thread.ofVirtual().start(new Hydrogen(i));
				i++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		Thread.ofVirtual().start(() -> {
			int i=1;
			while(!Thread.currentThread().isInterrupted()) {
				Thread.ofVirtual().start(new Oxygen(i));
				i++;
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		Thread.sleep(20000);
	}

	static final class Hydrogen implements Runnable {
		private final int id;

		public Hydrogen(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			try {
				hydMutex.acquire();
				mutex.acquire();
				hydCount++;
				if(hydCount == 2) {
					hyd.release();
				} else {
					hydMutex.release();
				}
				mutex.release();

				oxy.acquire();
				bond();
				barrier.acquire();

				mutex.acquire();
				hydCount--;
				barrier.release();
				if(hydCount == 0) hydMutex.release();
				mutex.release();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		private void bond() {
			System.out.println("hydrogen-" + id +  " is ready to bond");
		}
	}

	static final class Oxygen implements Runnable {
		private final int id;

		public Oxygen(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			try {
				hyd.acquire();
				oxy.release(2);
				bond();
				barrier.acquire();

				barrier.release();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		private void bond() {
			System.out.println("Oxygen-" + id + " is ready to bond");
		}
	}
}
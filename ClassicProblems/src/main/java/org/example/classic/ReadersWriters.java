package org.example.classic;

import java.util.concurrent.Semaphore;

public class ReadersWriters {
	private static final Semaphore roomEmpty = new Semaphore(1);
	private static final Semaphore turnstile = new Semaphore(1);

	public static void main(String[] args) throws InterruptedException {
		Thread.ofVirtual().start(() -> {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					Thread.ofVirtual().start(new Writers());
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		Thread.ofVirtual().start(() -> {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					Thread.ofVirtual().start(new Readers());
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		Thread.sleep(20000);

	}


	static class Writers implements Runnable {
		@Override
		public void run() {
			try {
				turnstile.acquire(); // stops any reader threads from entering
				roomEmpty.acquire(); // waits for readers threads to exit
				System.out.println("Writing to the critical section");
				Thread.sleep(3000);
				roomEmpty.release();
				turnstile.release();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static class Readers implements Runnable {
		private final LightSwitch lightSwitch;
		public Readers() {
			this.lightSwitch = new LightSwitch();
		}

		@Override
		public void run() {
			try {
				turnstile.acquire();
				turnstile.release();

				lightSwitch.lock(roomEmpty);
				System.out.println("Reading the section");
				Thread.sleep(1000);
				lightSwitch.unlock(roomEmpty);

			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static final class LightSwitch {
		private final Semaphore mutex;
		private int counter;

		public LightSwitch() {
			mutex = new Semaphore(1);
			counter = 0;
		}

		public void lock(Semaphore semaphore) throws InterruptedException {
			mutex.acquire();
			counter++;
			if(counter == 1) {
				semaphore.acquire();
			}
			mutex.release();
		}

		public void unlock(Semaphore semaphore) throws InterruptedException {
			mutex.acquire();
			counter--;
			if(counter == 0) semaphore.release();
			mutex.release();
		}
	}
}

package org.example.classic;

import java.util.concurrent.Semaphore;

// This solution would give writers to reach CS faster and thus might result in starvation of readers
public class ReadersWritersPriority {
	private static final Semaphore noReaders = new Semaphore(1);
	private static final Semaphore noWriters = new Semaphore(1);

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
		private final LightSwitch lightSwitch;

		public Writers() {
			this.lightSwitch = new LightSwitch();
		}

		@Override
		public void run() {
			try {
				lightSwitch.lock(noReaders); // we lock reader thread as soon as there is a writer thread
				noWriters.acquire(); // to sync all writer threads
				System.out.println("Writing to the critical section");
				Thread.sleep(3000);
				noWriters.release();
				lightSwitch.unlock(noReaders); // we are unblocking reader thread only once all the queued writer
				// threads have passed critical section
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
				noReaders.acquire(); // to sync readers
				lightSwitch.lock(noWriters); // block the writer threads till all the readers have exited
				noReaders.release();
				System.out.println("Reading the section");
				Thread.sleep(1000);
				lightSwitch.unlock(noWriters);
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

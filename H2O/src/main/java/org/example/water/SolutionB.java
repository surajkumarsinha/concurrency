package org.example.water;

import java.util.concurrent.Semaphore;

public class SolutionB {
	private static final Semaphore hyd = new Semaphore(0); // signal that 2 hydrogens are available now
	private static final Semaphore oxy = new Semaphore(0);
	private static final Semaphore mutex = new Semaphore(1);
	private static final Semaphore barrier = new Semaphore(3);
	private static int hydCount = 0;
	private static int oxyCount = 0;

	public static void main(String[] args) throws InterruptedException {
		Thread.ofVirtual().start(() -> {
			int i=1;
			while(!Thread.currentThread().isInterrupted()) {
				try {
					Thread.ofVirtual().start(new Hydrogen(i));
					i++;
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		Thread.ofVirtual().start(() -> {
			int i=1;
			while(!Thread.currentThread().isInterrupted()) {
				try {
					Thread.ofVirtual().start(new Oxygen(i));
					i++;
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
				mutex.acquire();
				hydCount++;
				if(hydCount >=2 && oxyCount >= 1) {
					hyd.release(2);
					hydCount-=2;
					oxy.release();
					oxyCount--;
				} else {
					mutex.release();
				}

				hyd.acquire();
				bond();

				barrier.acquire();
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
				mutex.acquire();
				oxyCount++;
				if(hydCount >= 2) {
					hyd.release(2);
					hydCount-=2;
					oxy.release();
					oxyCount--;
				} else {
					mutex.release();
				}

				oxy.acquire();
				bond();

				barrier.acquire();
				mutex.release();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		private void bond() {
			System.out.println("Oxygen-" + id + " is ready to bond");
		}
	}
}

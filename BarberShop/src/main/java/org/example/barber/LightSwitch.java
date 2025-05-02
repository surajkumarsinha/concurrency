package org.example.barber;

import java.util.concurrent.Semaphore;

public class LightSwitch {
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

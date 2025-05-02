package org.example.hilzer;

import java.util.concurrent.Semaphore;

public class Usher implements Runnable {
	private final BarberShop shop;

	public Usher(BarberShop shop) {
		this.shop = shop;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				shop.getSofaLine().acquire();
				Semaphore customerForSofa;
				shop.getMutex().acquire();
				customerForSofa = shop.getSofaQueue().poll();
				shop.getMutex().release();

				customerForSofa.release();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

package org.example.barber;

import java.util.concurrent.Semaphore;

public class Barber implements Runnable{
	private final BarberShop shop;

	public Barber(BarberShop shop) {
		this.shop = shop;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				shop.getCustomer().acquire();
				Semaphore sem;
				shop.getMutex().acquire();
				sem = shop.getQueue().poll();
				shop.getMutex().release();
				sem.release();

				cutHair();
				shop.getCustomerDone().acquire();
				shop.getBarberDone().release();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void cutHair() throws InterruptedException {
		Thread.sleep(3000);
	}
}

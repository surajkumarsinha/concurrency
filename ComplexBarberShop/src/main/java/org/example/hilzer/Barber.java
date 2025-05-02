package org.example.hilzer;

import java.util.concurrent.Semaphore;

public class Barber implements Runnable {
	private final BarberShop shop;
	private final int id;

	public Barber(BarberShop shop, int id) {
		this.shop = shop;
		this.id = id;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				shop.getSeatLine().acquire();
				Semaphore customerForSeat;
				shop.getMutex().acquire();
				customerForSeat = shop.getSeatQueue().poll();
				shop.getMutex().release();

				customerForSeat.release();

				System.out.println("Barber-" + id + " is cutting hair now");
				Thread.sleep(3000);

				shop.getPayment().acquire();
				System.out.println("Barber-" + id + " is accepting payment now");
				shop.getReceipt().release();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

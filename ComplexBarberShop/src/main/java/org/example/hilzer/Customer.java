package org.example.hilzer;

import java.util.concurrent.Semaphore;

public class Customer implements Runnable {
	private final int id;
	private final BarberShop shop;
	private final Semaphore seatSem;
	private final Semaphore sofaSem;

	public Customer(int id, BarberShop shop) {
		this.shop = shop;
		this.id = id;
		this.seatSem = new Semaphore(0);
		this.sofaSem = new Semaphore(0);
	}

	@Override
	public void run() {
		try {
			shop.getMutex().acquire();
			if(shop.isFull()) {
				shop.getMutex().release();
				balk();
			}
			shop.incrementCustomers();
			shop.getSofaQueue().add(sofaSem);
			shop.getMutex().release();

			enter();

			shop.getSofaLine().release(); // signaling that a customer for sofa is available
			sofaSem.acquire();
			shop.getSofa().acquire();
			sitOnSofa();
			sofaSem.release();

			shop.getMutex().acquire();
			shop.getSeatQueue().add(seatSem);
			shop.getMutex().release();

			shop.getSeatLine().release();
			seatSem.acquire();

			shop.getSofa().release();
			sitOnChair();
			seatSem.release();

			shop.getPayment().release();
			shop.getReceipt().acquire();

			shop.getMutex().acquire();
			shop.decrementCustomers();
			shop.getMutex().release();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void balk() {
		System.out.println("Cutomer-" + id + " cannot enter as the shop is full");
		Thread.currentThread().interrupt();
	}

	void enter() {
		System.out.println("Cutomer-" + id + " has entered the shop");
	}

	void sitOnSofa() {
		System.out.println("Cutomer-" + id + " is sitting on the sofa");
	}

	void sitOnChair() {
		System.out.println("Cutomer-" + id + " is sitting on the barber chair");
	}
}

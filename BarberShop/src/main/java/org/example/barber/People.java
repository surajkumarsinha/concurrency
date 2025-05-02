package org.example.barber;

import java.util.concurrent.Semaphore;

public class People implements Runnable {
	private final int id;
	private final BarberShop barberShop;
	private final Semaphore semaphore;

	public People(int id, BarberShop barberShop) {
		this.id = id;
		this.barberShop = barberShop;
		semaphore = new Semaphore(0);
	}

	@Override
	public void run() {
		try {
			barberShop.getMutex().acquire();
			if(barberShop.isShopFull()) {
				barberShop.getMutex().release();
				balk();
			}
			barberShop.incrementCustomer();
			barberShop.getQueue().add(semaphore);
			barberShop.getMutex().release();

			barberShop.getCustomer().release(); // notify barber that it is seated
			semaphore.acquire(); // barber picks up this customer for hair cut

			getHaircut();

			barberShop.getCustomerDone().release(); // customer haircut is done
			barberShop.getBarberDone().acquire();  // barber has acknowledged the haircut

			barberShop.getMutex().acquire();
			barberShop.decrementCustomer();
			barberShop.getMutex().release();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void getHaircut() throws InterruptedException {
		System.out.println("The customer with id: " +  id + " is getting a haircut");
		Thread.sleep(1000);
	}

	private void balk() throws InterruptedException {
		System.out.println("The barber shop is full so customer-" + id + " cannot get haircut");
		Thread.currentThread().interrupt();
	}
}

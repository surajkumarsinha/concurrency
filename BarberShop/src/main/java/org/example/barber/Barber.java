package org.example.barber;

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
				shop.getBarber().release();
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

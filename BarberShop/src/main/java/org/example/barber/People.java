package org.example.barber;

public class People implements Runnable {
	private final int id;
	private final BarberShop barberShop;

	public People(int id, BarberShop barberShop) {
		this.id = id;
		this.barberShop = barberShop;
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
			barberShop.getMutex().release();

			barberShop.getCustomer().release(); // notify barber that it is seated
			barberShop.getBarber().acquire(); // barber picks up this customer for hair cut

			getHaircut();

			barberShop.getCustomerDone().release(); // customer haircut is done
			barberShop.getBarberDone().acquire();  // barber has acknowledged the haircut

			barberShop.getMutex().acquire();
			barberShop.decrementCustomer();
			barberShop.getMutex().release();

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
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

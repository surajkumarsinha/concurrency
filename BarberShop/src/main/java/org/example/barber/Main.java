package org.example.barber;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		BarberShop barberShop = new BarberShop(5);
		Thread.ofVirtual().start(new Barber(barberShop));
		Thread.ofVirtual().start(() -> {
			int id = 1;
			while(!Thread.currentThread().isInterrupted()) {
				Thread.ofVirtual().start(new People(id, barberShop));
				id++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
		Thread.sleep(25000);
	}
}
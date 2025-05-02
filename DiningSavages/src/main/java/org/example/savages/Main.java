package org.example.savages;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		DiningHall hall = new DiningHall(5);
		Thread.ofVirtual().start(new Cook(hall));
		Thread.ofVirtual().start(() -> {
			int id = 1;
			while(!Thread.currentThread().isInterrupted()) {
				Thread.ofVirtual().start(new Savage(hall, id));
				id++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
		Thread.sleep(20000);
	}
}
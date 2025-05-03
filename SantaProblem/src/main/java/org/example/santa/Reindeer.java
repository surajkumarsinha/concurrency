package org.example.santa;

public class Reindeer implements Runnable {
	private final int id;
	private final SantaShop shop;

	public Reindeer(int id, SantaShop shop) {
		this.id = id;
		this.shop = shop;
	}

	@Override
	public void run() {
		try {
			shop.mutex.acquire();
			shop.reindeers++;
			if(shop.allReindeersArrived()) {
				System.out.println("All reindeers available now");
				shop.santa.release();
			}
			shop.mutex.release();
			shop.reindeerSem.acquire();
			getHitched();
			shop.flySem.release();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void getHitched() throws InterruptedException {
		System.out.println("Reindeer-" + id + " is hitched to the santa saddle");
		Thread.sleep(500);
	}
}

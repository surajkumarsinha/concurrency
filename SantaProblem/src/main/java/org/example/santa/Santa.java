package org.example.santa;

public class Santa implements Runnable {
	private final SantaShop shop;

	public Santa(SantaShop shop) {
		this.shop = shop;
	}

	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				shop.santa.acquire();
				shop.mutex.acquire();

				if(shop.allReindeersArrived()) {
					prepareSledge();
					int totalReindeers = shop.reindeers;
					shop.reindeerSem.release(totalReindeers);
					shop.reindeers = 0;
					shop.flySem.acquire(totalReindeers);
					shop.mutex.release();
					fly();
				} else if (shop.minElfCountReached()) {
					helpElves();
					shop.elfSem.release();
					shop.mutex.release();
				} else {
					shop.mutex.release();
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void prepareSledge() throws InterruptedException {
		System.out.println("Santa is preparing sledge");
		Thread.sleep(3000);
	}

	private void helpElves() throws InterruptedException {
		System.out.println("Santa is helping elves");
		Thread.sleep(2000);
	}

	private void fly() throws InterruptedException {
		System.out.println("Santa is going away to distribute presents");
		Thread.sleep(5000);
	}
}

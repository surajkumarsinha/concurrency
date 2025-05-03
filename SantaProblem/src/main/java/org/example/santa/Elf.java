package org.example.santa;

public class Elf implements Runnable {
	private final int id;
	private final SantaShop shop;

	public Elf(int id, SantaShop shop) {
		this.id = id;
		this.shop = shop;
	}

	@Override
	public void run() {
		try {
			shop.elfMutex.acquire();
			shop.mutex.acquire();
			shop.elves++;
			if(shop.minElfCountReached()) {
				shop.santa.release();
			} else {
				shop.elfMutex.release();
			}
			shop.mutex.release();

			gethelp();
			shop.elfSem.acquire();
			System.out.println("Elf-" + id + " is getting help from Santa");

			shop.mutex.acquire();
			shop.elves--;
			if(shop.elves == 0) shop.elfMutex.release();
			else shop.elfSem.release();
			shop.mutex.release();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void gethelp() {
		System.out.println("Elf-" + id + " asking for help");
	}
}

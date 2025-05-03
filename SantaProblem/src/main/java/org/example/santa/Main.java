package org.example.santa;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		SantaShop santaShop = new SantaShop(9, 3);
		Thread.ofVirtual().start(() -> {
			int i = 1;
			while(!Thread.currentThread().isInterrupted()) {
				Thread.ofVirtual().start(new Reindeer(i, santaShop));
				i++;
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
		Thread.ofVirtual().start(() -> {
			int i=1;
			while(!Thread.currentThread().isInterrupted()) {
				Thread.ofVirtual().start(new Elf(i, santaShop));
				i++;
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});
		Thread.ofVirtual().start(new Santa(santaShop));

		Thread.sleep(30000);
	}
}
package org.example.savages;

public class Cook implements Runnable {

	private final DiningHall hall;
	public Cook(DiningHall hall) {
		this.hall = hall;
	}

	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				hall.getEmptyPot().acquire();
				hall.resetServings();
				System.out.println("Cooking the meal again since it is empty");
				Thread.sleep(4000);
				hall.getFullPot().release();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

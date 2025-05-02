package org.example.savages;

public class Savage implements Runnable {
	private final DiningHall hall;
	private final int id;

	public Savage(DiningHall hall, int id) {
		this.hall = hall;
		this.id = id;
	}

	@Override
	public void run() {
		try {
			hall.getMutex().acquire();
			if(hall.getServingsCount() == 0) {
				hall.getEmptyPot().release();
				hall.getFullPot().acquire();
			}
			hall.decrementServings();
			hall.getMutex().release();
			eat();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void eat() throws InterruptedException {
		System.out.println("Savage-" + id + " is eating");
		Thread.sleep(1000);
	}
}

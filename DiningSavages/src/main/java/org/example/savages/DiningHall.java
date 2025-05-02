package org.example.savages;

import java.util.concurrent.Semaphore;

public class DiningHall {
	private final int servings;
	private int servingsCount;
	private final Semaphore emptyPot;
	private final Semaphore fullPot;
	private final Semaphore mutex;

	public DiningHall(int servings) {
		this.servings = servings;
		this.servingsCount = 0;
		this.emptyPot = new Semaphore(0);
		this.fullPot = new Semaphore(0);
		this.mutex = new Semaphore(1);
	}

	public int getServingsCount() {
		return servingsCount;
	}

	public void resetServings() {
		servingsCount = servings;
	}

	public void decrementServings() {
		servingsCount--;
	}

	public Semaphore getEmptyPot() {
		return emptyPot;
	}

	public Semaphore getFullPot() {
		return fullPot;
	}

	public Semaphore getMutex() {
		return mutex;
	}
}

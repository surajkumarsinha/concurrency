package org.example.santa;

import java.util.concurrent.Semaphore;

public class SantaShop {
	public int reindeers = 0;
	public int elves = 0;
	public Semaphore santa = new Semaphore(0);
	public Semaphore mutex = new Semaphore(1); // to update common data
	public Semaphore reindeerSem = new Semaphore(0);
	public Semaphore elfMutex = new Semaphore(1);
	public Semaphore elfSem = new Semaphore(0);
	public Semaphore flySem = new Semaphore(0);

	private final int totalReindeer;
	private final int minElvesToWakeSanta;

	public SantaShop(int totalReindeer, int minElvesToWakeSanta) {
		this.totalReindeer = totalReindeer;
		this.minElvesToWakeSanta = minElvesToWakeSanta;
	}

	public boolean allReindeersArrived() {
		return reindeers == totalReindeer;
	}

	public boolean minElfCountReached() {
		return elves == minElvesToWakeSanta;
	}
}

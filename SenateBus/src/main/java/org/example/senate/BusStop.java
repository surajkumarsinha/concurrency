package org.example.senate;

import java.util.concurrent.Semaphore;

public class BusStop {
	private final Semaphore boardingSem;
	private final Semaphore waitingAreaSem;
	private final Semaphore busDepartureSem;
	private final Semaphore busOrRiders;
	private static int ridersCount = 0;

	public BusStop(int maxCapacity) {
		boardingSem = new Semaphore(0);
		waitingAreaSem = new Semaphore(maxCapacity);
		busDepartureSem = new Semaphore(0);
		busOrRiders = new Semaphore(1);
	}

	public static void incrementRiders() {
		ridersCount++;
	}

	public static int getRidersCount() {return ridersCount;}

	public static void decrementRiders() {
		ridersCount--;
	}

	public Semaphore getBoardingSem() {
		return boardingSem;
	}

	public Semaphore getWaitingAreaSem() {
		return waitingAreaSem;
	}

	public Semaphore getBusDepartureSem() {
		return busDepartureSem;
	}

	public Semaphore getBusOrRiders() {
		return busOrRiders;
	}
}

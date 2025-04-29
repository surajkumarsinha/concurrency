package org.example.senate;

public class Rider implements Runnable {
	private final int id;
	private final BusStop busStop;

	public Rider(int id, BusStop busStop) {
		this.id = id;
		this.busStop = busStop;
	}

	@Override
	public void run() {
		try {
			busStop.getWaitingAreaSem().acquire();
			// To enter the boarding area and after the rider has entered the area, bus can enter the area
			busStop.getBusOrRiders().acquire();
			enterBoardingArea();
			BusStop.incrementRiders();
			busStop.getBusOrRiders().release();

			/* Acquiring the semaphore to board the bus. This is similar to opening the door of the bus
			The semaphore is first released by the bus to signal the rider/s to board.
			After acquiring the semaphore, rider exits the waiting area and board to the bus
			*/
			busStop.getBoardingSem().acquire();
			boardBus();
			busStop.getWaitingAreaSem().release();

			// Rider has boarded the bus and the waiting area can accept more people
			BusStop.decrementRiders();

			if(BusStop.getRidersCount() == 0) busStop.getBusDepartureSem().release();
			else busStop.getBoardingSem().release(); // the next person can board now

		} catch(InterruptedException e) {
			throw new RuntimeException(e);
		}
	}


	public void boardBus() {
		System.out.println("Rider id :" + id + " boarded.");
	}

	public void enterBoardingArea() {
		System.out.println("Rider id :" + id + " entered boarding area.");
	}
}

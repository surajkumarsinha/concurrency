package org.example.senate;

public class Bus implements Runnable {
	private final int id;
	private final BusStop busStop;

	public Bus(int id, BusStop busStop) {
		this.id = id;
		this.busStop = busStop;
	}

	@Override
	public void run() {
		try {
			busStop.getBusOrRiders().acquire();
			arrived();
			if(BusStop.getRidersCount() > 0) {
				busStop.getBoardingSem().release();
				busStop.getBusDepartureSem().acquire();
			}
			depart();
			busStop.getBusOrRiders().release();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void depart() {
		System.out.println("<<<<<     Bus id : " + id + " departed     >>>>>\n");
	}

	public void arrived() {
		System.out.println("\n<<<<<     Bus id : " + id + " arrived     >>>>>");
		System.out.println("<<<<<     Waiting rider count is : " + BusStop.getRidersCount() + "     >>>>>");
	}
}

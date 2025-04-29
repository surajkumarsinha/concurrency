package org.example.senate;

import java.util.Random;

public interface Generator extends Runnable {
	Random RANDOM =  new Random();

	default long getWaitingTime(float arrivalMeanTime){
		float lambda = 1 / arrivalMeanTime;
		return Math.round(-Math.log(1 - RANDOM.nextFloat()) / lambda);
	}

	static Generator createBus(float busArrivalMeanTime, BusStop busStop) {
		return new BusGenerator(busArrivalMeanTime, busStop);
	}

	static Generator createRider(float riderArrivalTime, BusStop busStop) {
		return new RiderGenerator(riderArrivalTime, busStop);
	}

	final class BusGenerator implements Generator {

		private final float busArrivalMeanTime;
		private final BusStop busStop;

		public BusGenerator(float busArrivalMeanTime, BusStop busStop) {
			this.busArrivalMeanTime = busArrivalMeanTime;
			this.busStop = busStop;
		}

		@Override
		public void run() {
			int busCount = 1;
			while(!Thread.currentThread().isInterrupted()) {
				Bus bus = new Bus(busCount, busStop);
				Thread.ofVirtual().start(bus);
				busCount++;
				try {
					long wt = getWaitingTime(busArrivalMeanTime);
					System.out.println("\n<<<<<     Next bus in " + wt + "ms     >>>>>");
					Thread.sleep(wt);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	final class RiderGenerator implements Generator {
		private final float riderArivalMeanTime;
		private final BusStop busStop;

		public RiderGenerator(float riderArrivalMeanTime, BusStop busStop) {
			this.riderArivalMeanTime = riderArrivalMeanTime;
			this.busStop = busStop;
		}

		@Override
		public void run() {

			int riderCount = 1;
			// Spawning rider threads for the user specified value
			while (!Thread.currentThread().isInterrupted()) {

				try {
					// Initializing and starting the rider threads
					Rider rider = new Rider(riderCount, busStop);
					Thread.ofVirtual().start(rider);
					riderCount++;
					// Sleeping the thread to obtain the inter arrival time between the threads
					Thread.sleep(getWaitingTime(riderArivalMeanTime));

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

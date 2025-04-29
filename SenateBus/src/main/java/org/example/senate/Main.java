package org.example.senate;


public class Main {
	public static void main(String[] args) throws InterruptedException {
		float riderArrivalMeanTime = 1000f;
		float busArrivalMeanTime = 20 * 1000f;

		BusStop busStop = new BusStop(10);
		Thread.ofVirtual().start(Generator.createBus(busArrivalMeanTime, busStop));
		Thread.ofVirtual().start(Generator.createRider(riderArrivalMeanTime, busStop));

		Thread.sleep(50000);
	}
}
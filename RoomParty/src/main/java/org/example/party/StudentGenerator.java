package org.example.party;

import java.util.Random;

public class StudentGenerator implements Runnable {

	private final PartyRoom partyRoom;
	private final float arrivalTime;
	private int studentId;
	private static Random RANDOM =  new Random();

	public StudentGenerator(PartyRoom partyRoom, float arrivalTime) {
		this.partyRoom = partyRoom;
		this.studentId = 0;
		this.arrivalTime = arrivalTime;
	}

	@Override
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()) {
				Thread.ofVirtual().start(new Student(studentId, partyRoom));
				studentId++;
				Thread.sleep(getWaitingTime());
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	long getWaitingTime(){
		float lambda = 1 / arrivalTime;
		return Math.round(-Math.log(1 - RANDOM.nextFloat()) / lambda);
	}
}

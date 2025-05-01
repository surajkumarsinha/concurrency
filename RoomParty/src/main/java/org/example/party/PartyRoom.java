package org.example.party;

import java.util.concurrent.Semaphore;

public class PartyRoom {
	private int students;
	private final Semaphore deanEntry;
	private final Semaphore deanExit;
	private final Semaphore mutex;
	private final Semaphore turn;
	private final int safeRoomLimit;
	private DeanState deanState;

	public PartyRoom(int safeRoomLimit) {
		this.deanEntry = new Semaphore(0); // to signal dean can enter
		this.deanExit = new Semaphore(0); // to signal dean can exit
		this.mutex = new Semaphore(1); // To perform operations to the party room
		this.turn = new Semaphore(1); // to signal who would be in the room
		this.safeRoomLimit = safeRoomLimit;
		this.students = 0;
	}

	public void incrementStudents() {
		students++;
	}

	public void decrementStudents() {
		students--;
	}

	public int getStudents() {
		return students;
	}

	public Semaphore getDeanEntry() {
		return deanEntry;
	}

	public Semaphore getDeanExit() {
		return deanExit;
	}

	public int getSafeRoomLimit() {
		return safeRoomLimit;
	}

	public Semaphore getMutex() {
		return mutex;
	}

	public boolean canInspect() {
		return canSearchRoom() || canBreakParty();
	}

	public boolean canSearchRoom() {
		return students == 0;
	}

	public boolean canBreakParty() {
		return students > safeRoomLimit;
	}

	public Semaphore getTurn() {
		return turn;
	}

	public DeanState getDeanState() {
		return deanState;
	}

	public void setDeanState(DeanState deanState) {
		this.deanState = deanState;
	}
}

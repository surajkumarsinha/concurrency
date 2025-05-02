package org.example.hilzer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BarberShop {
	private final int capacity;
	private final int seats;
	private int occupancy;

	private final Queue<Semaphore> seatQueue; // queue for the barber seat
	private final Queue<Semaphore> sofaQueue; // queue for the sofa

	private final Semaphore payment;
	private final Semaphore barber;
	private final Semaphore seatLine; // indicates a person has entered the room
	private final Semaphore sofaLine; // indicates that a person is now sitting on the sofa
	private final Semaphore sofa;
	private final Semaphore receipt;

	private final Semaphore mutex;

	public Semaphore getReceipt() {
		return receipt;
	}

	public BarberShop(int capacity, int seats) {
		this.capacity = capacity;
		this.seats = seats;
		this.occupancy = 0;

		this.seatQueue = new LinkedList<>();
		this.sofaQueue = new LinkedList<>();

		this.payment = new Semaphore(0);
		this.barber = new Semaphore(0);
		this.seatLine = new Semaphore(0);
		this.sofaLine = new Semaphore(0);
		this.sofa = new Semaphore(4);
		this.receipt = new Semaphore(0);

		this.mutex = new Semaphore(1);
	}

	public boolean isFull() {
		return occupancy == capacity;
	}

	public void incrementCustomers() {
		occupancy++;
	}

	public void decrementCustomers() {
		occupancy--;
	}

	public Queue<Semaphore> getSeatQueue() {
		return seatQueue;
	}

	public Queue<Semaphore> getSofaQueue() {
		return sofaQueue;
	}

	public Semaphore getPayment() {
		return payment;
	}

	public Semaphore getBarber() {
		return barber;
	}

	public Semaphore getSeatLine() {
		return seatLine;
	}

	public Semaphore getSofaLine() {
		return sofaLine;
	}

	public Semaphore getMutex() {
		return mutex;
	}

	public Semaphore getSofa() {
		return sofa;
	}
}

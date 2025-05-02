package org.example.barber;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BarberShop {
	private final int capacity;
	private int occupancy;
	private final Semaphore mutex;
	private final Semaphore customer;
	private final Semaphore barberDone;
	private final Semaphore customerDone;
	private final Queue<Semaphore> queue;

	public BarberShop(int capacity) {
		this.capacity = capacity;
		this.occupancy = 0;
		this.customer = new Semaphore(0);
		this.barberDone = new Semaphore(0);
		this.customerDone = new Semaphore(0);
		this.mutex = new Semaphore(1);
		this.queue = new LinkedList<>();
	}

	public void incrementCustomer() {
		occupancy++;
	}
	public void decrementCustomer() {
		occupancy--;
	}

	public boolean isShopFull() {
		return occupancy == capacity;
	}

	public Semaphore getMutex() {
		return mutex;
	}

	public Semaphore getCustomer() {
		return customer;
	}

	public Semaphore getBarberDone() {
		return barberDone;
	}

	public Semaphore getCustomerDone() {
		return customerDone;
	}

	public Queue<Semaphore> getQueue() {
		return queue;
	}
}

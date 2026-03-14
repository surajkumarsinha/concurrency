package org.example;

import java.util.concurrent.Semaphore;

public class ReentrantMutex {
	private final Semaphore mutex;
	private Integer holdCount;
	private Thread owner;

	public ReentrantMutex() {
		this.mutex = new Semaphore(1);
		holdCount = 0;
		owner = null;
	}

	public void lock() throws InterruptedException {
		Thread current = Thread.currentThread();

		if (current == owner) {
			holdCount++;
			return;
		}

		mutex.acquire();
		owner = current;
		holdCount = 1;

	}

	public void unlock() {
			Thread current = Thread.currentThread();
			if (current != owner) {
				throw new IllegalMonitorStateException();
			}
			holdCount--;
			if (holdCount == 0) {
				owner = null;
				mutex.release();
			}
	}
}

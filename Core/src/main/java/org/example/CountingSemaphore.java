package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CountingSemaphore {

	private final int maxPermits;
	private int permits;

	private final Lock lock = new ReentrantLock();
	private final Condition condition = lock.newCondition();

	public CountingSemaphore(int permits) {
		this.maxPermits = permits;
		this.permits = permits;
	}

	public void acquire() throws InterruptedException {
		lock.lock();
		try {
			while (permits == 0) {
				condition.await();
			}
			permits--;
		} finally {
			lock.unlock();
		}
	}

	public void release() {
		lock.lock();
		try {
			if (permits == maxPermits) {
				throw new IllegalStateException("Semaphore released too many times");
			}
			permits++;
			condition.signal();
		} finally {
			lock.unlock();
		}
	}
}

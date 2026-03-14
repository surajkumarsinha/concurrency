package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

interface Awaitable {
	void await() throws InterruptedException;

	final class CyclicBarrier implements Awaitable {
		private final int originalCount;
		private int count;
		int generation;
		private final Lock lock;
		private final Condition releaseCondition;

		public CyclicBarrier(int count) {
			this.lock = new ReentrantLock();
			this.releaseCondition = lock.newCondition();
			this.count = count;
			this.originalCount = count;
			this.generation = 0;
		}

		@Override
		public void await() throws InterruptedException {
			lock.lock();
			try {
				int curGeneration = generation;
				count--;
				if (count == 0) {
					generation++;
					count = originalCount;
					releaseCondition.signalAll();
					return;
				}
				while (curGeneration == generation) {
					releaseCondition.await();
				}
			} finally {
				lock.unlock();
			}
		}
	}
}

interface CountDownLatch extends Awaitable {
	void countDown();

	final class CountDownLatchImpl implements CountDownLatch {
		private int permits;
		private final Lock lock;
		private final Condition releaseCondition;

		public CountDownLatchImpl(int count) {
			this.lock = new ReentrantLock();
			this.releaseCondition = lock.newCondition();
			this.permits = count;
		}

		@Override
		public void countDown() {
			lock.lock();
			try {
				if (permits == 0) return;
				permits--;
				if (permits == 0) releaseCondition.signalAll();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void await() throws InterruptedException {
			lock.lock();
			try {
				while (permits > 0)
					releaseCondition.await();
			} finally {
				lock.unlock();
			}
		}
	}
}


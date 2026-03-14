package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public interface BoundedBlockingQueue<T> {
	void put(T item) throws InterruptedException;
	T take() throws InterruptedException;

	final class BoundedBlockingQueueWithLocks<T> implements BoundedBlockingQueue<T> {
		private final int maxCapacity;
		private final Queue<T> queue;
		private final Lock lock;
		private final Condition notEmpty;
		private final Condition notFull;

		public BoundedBlockingQueueWithLocks(int maxCapacity) {
			this.maxCapacity = maxCapacity;
			this.queue = new LinkedList<>();
			this.lock = new ReentrantLock();
			this.notEmpty = lock.newCondition();
			this.notFull = lock.newCondition();
		}

		@Override
		public void put(T item) throws InterruptedException {
			lock.lock();
			try {
				while(queue.size() == maxCapacity) notFull.await();
				queue.add(item);
				notEmpty.signal();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public T take() throws InterruptedException {
			lock.lock();
			try {
				while (queue.isEmpty()) notEmpty.await();
				T value = queue.poll();
				notFull.signal();
				return value;
			} finally {
				lock.unlock();
			}
		}
	}

	final class BoundedBlockingQueueWithSemaphores<T> implements BoundedBlockingQueue<T> {
		private final Queue<T> queue;
		private final Semaphore fullSlots;
		private final Semaphore emptySlots;
		private final Semaphore mutex;

		public BoundedBlockingQueueWithSemaphores(int maxCapacity) {
			queue = new LinkedList<>();
			fullSlots = new Semaphore(0);
			emptySlots = new Semaphore(maxCapacity);
			mutex = new Semaphore(1);
		}

		@Override
		public void put(T item) throws InterruptedException {
			emptySlots.acquire();
			mutex.acquire();
			try {
				queue.add(item);
			} finally {
				mutex.release();
			}
			fullSlots.release(); // item is available
		}

		@Override
		public T take() throws InterruptedException {
			fullSlots.acquire(); // wait for items to be available
			mutex.acquire();
			T item;
			try {
				item = queue.poll();
			} finally {
				mutex.release();
			}
			emptySlots.release(); // space available
			return item;
		}
	}
}
/*
*
* Acquire in this order:
		resource semaphore → mutex
	Never:
		mutex → resource semaphore

Otherwise you can create deadlocks.
*
* */
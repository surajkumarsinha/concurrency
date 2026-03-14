package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public interface ThreadPool {

	void submit(Runnable task) throws InterruptedException;
	void shutDown();

	final class FixedThreadPool implements ThreadPool {
		private final BlockingQueue<Runnable> taskQueue;
		private final List<Worker> workers;
		private volatile boolean isAlive;


		public FixedThreadPool(int maxCapacity) {
			this.taskQueue = new ArrayBlockingQueue<>(maxCapacity);
			this.isAlive = true;
			this.workers = new ArrayList<>();

			for(int i=0; i < maxCapacity; i++) {
				Worker worker = new Worker();
				workers.add(worker);
				worker.start();
			}
		}

		@Override
		public void submit(Runnable task) throws InterruptedException {
			if (!isAlive)
				throw new IllegalStateException("ThreadPool is shutdown");
			taskQueue.offer(task);
		}

		@Override
		public void shutDown() {
			isAlive = false;
			for(Thread worker: workers) worker.interrupt();
		}

		private class Worker extends Thread {
			public void run() {
				while(true) {
					try {
						Runnable runnable = taskQueue.take();
						runnable.run();
					} catch (InterruptedException e) {
						if(!isAlive && taskQueue.isEmpty())
							break;
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}



package org.example.classic;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ProducerConsumer {
	private static final Semaphore items = new Semaphore(0);
	private static final Semaphore bufferSize = new Semaphore(5);
	private static final Semaphore mutex = new Semaphore(1);
	private static final Queue<String> events = new LinkedList<>();

	public static void main(String[] args) throws InterruptedException {
		Thread.ofVirtual().start(new Producer());
		Thread.ofVirtual().start(new Consumer());
		Thread.sleep(20000);
	}


	static class Producer implements Runnable {
		int count = 0;
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					String event = produceEvent(count);
					bufferSize.acquire();
					mutex.acquire();
					events.add(event);
					mutex.release();
					items.release();
					count++;
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

			}
		}

		private String produceEvent(int count) throws InterruptedException {
			Thread.sleep(500);
			String event = "Event-" + count;
			System.out.println("Produced: " + event);
			return event;
		}
	}

	static class Consumer implements Runnable {
		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					items.acquire();
					mutex.acquire();
					String event = events.poll();
					mutex.release();
					bufferSize.release();
					consumeEvent(event);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		private void consumeEvent(String event) throws InterruptedException {
			Thread.sleep(1000);
			System.out.println("Consumed: " + event);
		}
	}
}
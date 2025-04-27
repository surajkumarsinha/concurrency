package org.example.unisex;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Person implements Runnable {

	private static final Logger logger = Logger.getLogger(Person.class.getName());
	public static final Random RANDOM = new Random();
	private final String id;
	private final Gender gender;
	private final CountDownLatch latch;
	private PersonStatusCallback statusCallback;

	public Person(String id, Gender gender) {
		this.id = id;
		this.gender = gender;
		this.latch = new CountDownLatch(1);
	}

	@Override
	public void run() {
		useBathroom();
		leaveBathroom();
	}

	private void useBathroom() {
		logger.log(Level.INFO, String.format("Person with id: %s and gender %s needs bathroom", this.id, this.gender.name()));
		try {
			latch.await();
			TimeUnit.SECONDS.sleep(RANDOM.nextInt(1,4));
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	private void leaveBathroom() {
		statusCallback.remove(this);
	}

	public Gender getGender() {
		return this.gender;
	}

	public void assign(PersonStatusCallback statusCallback) {
		this.statusCallback = statusCallback;
		latch.countDown();
	}

	public String getId() {
		return id;
	}
}

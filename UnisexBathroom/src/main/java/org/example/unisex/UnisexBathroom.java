package org.example.unisex;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnisexBathroom implements Bathroom {

	private Gender gender;
	private final Lock lock;
	private final Bathroom defaultBathroom;
	private static final Logger logger = Logger.getLogger(UnisexBathroom.class.getName());

	public UnisexBathroom(String id, int capacity) {
		this.gender = Gender.NONE;
		this.lock = new ReentrantLock();
		this.defaultBathroom = new DefaultBathroom(id, Gender.NONE, capacity);
	}

	@Override
	public void add(Person person) {
		logger.log(Level.INFO, String.format("Person %s is using unisex bathroom %s", person.getId(), defaultBathroom.getId()));
		lockedOperations(() -> {
			gender = person.getGender();
			defaultBathroom.add(person);
		});
	}

	@Override
	public boolean remove(Person person) {
		logger.log(Level.INFO, String.format("Person %s is removed from unisex bathroom %s", person.getId(), defaultBathroom.getId()));
		return lockedOperations(() -> {
			boolean isBathroomEmpty = defaultBathroom.remove(person);
			if(isBathroomEmpty) gender = Gender.NONE;
			return isBathroomEmpty;
		});
	}

	@Override
	public boolean canAccept(Gender gender) {
		return defaultBathroom.canAccept(gender) && (this.gender.equals(gender) || this.gender.equals(Gender.NONE));
	}

	@Override
	public void addHandler(BathroomStatusCallback bathroomStatusCallback) {
		defaultBathroom.addHandler(bathroomStatusCallback);
	}

	private void lockedOperations(Runnable runnable) {
		lock.lock();
		runnable.run();
		lock.unlock();
	}

	private <T> T lockedOperations(Supplier<T> supplier) {
		T result;
		lock.lock();
		result = supplier.get();
		lock.unlock();
		return result;
	}

	@Override
	public String getId() {
		return defaultBathroom.getId();
	}

	@Override
	public Gender getAssignedGender() {
		return defaultBathroom.getAssignedGender();
	}

}

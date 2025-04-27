package org.example.unisex;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class GenderedBathRoom implements Bathroom {
	private final Gender gender;
	private final Lock lock;
	private final Bathroom defaultBathroom;

	public GenderedBathRoom(String id, Gender gender, int capacity) {
		this.gender = gender;
		this.lock = new ReentrantLock();
		this.defaultBathroom = new DefaultBathroom(id, gender, capacity);
	}

	public GenderedBathRoom(String id, int capacity) {
		this(id, Gender.NONE, capacity);
	}


	@Override
	public void add(Person person) {
		lockedOperations(() -> defaultBathroom.add(person));
	}

	@Override
	public boolean remove(Person person) {
		return lockedOperations(() -> defaultBathroom.remove(person));
	}


	@Override
	public boolean canAccept(Gender gender) {
		return defaultBathroom.canAccept(gender) && this.gender.equals(gender);
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

package org.example.unisex;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class GenderWorker implements BathroomHandler, Runnable {

	private final LinkedBlockingQueue<Person> incoming;
	private final Gender gender;
	private final Set<Bathroom> bathRooms;
	private final Set<Bathroom> occupiedBathRooms;
	private final BathroomHandler unisexHandler;
	private final Lock lock = new ReentrantLock();

	public GenderWorker(
		LinkedBlockingQueue<Person> incoming,
		Set<Bathroom> bathRooms,
		BathroomHandler bathroomHandler,
		Gender gender
	) {
		this.incoming = incoming;
		this.gender = gender;
		this.bathRooms = bathRooms;
		this.unisexHandler = bathroomHandler;
		this.occupiedBathRooms = ConcurrentHashMap.newKeySet();
	}

	@Override
	public void run() {
		try {
			Person scheduledPerson = null;
			while (true) {
				Person person = scheduledPerson == null? incoming.take(): scheduledPerson;
				lock.lock();
				Bathroom bathRoom = parse(gender);
				if(bathRoom != null) bathRoom.add(person);
				else scheduledPerson = person;
				lock.unlock();
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Bathroom parse(Gender gender) {
		for(Bathroom b: bathRooms) if(b.canAccept(gender)) return b;
		return unisexHandler.parse(gender);
	}

	@Override
	public void occupied(String id, Bathroom bathRoom) {
		lock.lock();
		bathRooms.remove(bathRoom);
		occupiedBathRooms.add(bathRoom);
		lock.unlock();
	}

	@Override
	public void open(String id, Bathroom bathRoom) {
		lock.lock();
		occupiedBathRooms.remove(bathRoom);
		bathRooms.add(bathRoom);
		lock.unlock();
	}

	@Override
	public void addBathrooms(List<Bathroom> bs) {
		bs.forEach(b -> b.addHandler(this));
		bathRooms.addAll(bs);
	}

	@Override
	public void removeBathrooms(List<Bathroom> bs) {
		bs.forEach(bathRooms::remove);
	}

	@Override
	public void accept(Person person) {
		incoming.add(person);
	}
}

package org.example.unisex;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnisexHandler implements BathroomHandler {

	private final Set<Bathroom> bathRooms;
	private final Set<Bathroom> occupiedBathRooms;
	private final Lock lock = new ReentrantLock();
	private static final Logger logger = Logger.getLogger(UnisexHandler.class.getName());

	public UnisexHandler(Set<Bathroom> bathRooms) {
		this.bathRooms = bathRooms;
		this.occupiedBathRooms = ConcurrentHashMap.newKeySet();
	}

	@Override
	public Bathroom parse(Gender gender) {
		for(Bathroom b: bathRooms) if(b.canAccept(gender)) return b;
		return null;
	}

	@Override
	public void occupied(String id, Bathroom bathRoom) {
		logger.log(Level.INFO, String.format("Bathroom %s is full", id));
		lock.lock();
		bathRooms.remove(bathRoom);
		occupiedBathRooms.add(bathRoom);
		lock.unlock();
	}

	@Override
	public void open(String id, Bathroom bathRoom) {
		logger.log(Level.INFO, String.format("Bathroom %s is available now", id));
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
	public void removeBathrooms(List<Bathroom> bathRooms) {
	}

	@Override
	public void accept(Person person) {}
}

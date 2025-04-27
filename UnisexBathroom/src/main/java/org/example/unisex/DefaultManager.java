package org.example.unisex;

import java.util.*;
import java.util.concurrent.*;

public class DefaultManager implements Manager {

	private final Map<String, Bathroom> bathRoomMap;
	private final GenderWorker maleWorker;
	private final GenderWorker femaleWorker;
	private static final BathroomHandler UNISEX_HANDLER = new UnisexHandler(ConcurrentHashMap.newKeySet());

	public DefaultManager() {
		this.bathRoomMap = new ConcurrentHashMap<>();
		this.maleWorker = createWorker(Gender.MALE);
		this.femaleWorker = createWorker(Gender.FEMALE);

		Thread.ofVirtual().name("maleWorker").start(maleWorker);
		Thread.ofVirtual().name("femaleWorker").start(femaleWorker);
	}

	public void addBathrooms(List<Bathroom> newBathRooms) {
		List<Bathroom> mb = new ArrayList<>();
		List<Bathroom> fb = new ArrayList<>();
		List<Bathroom> ub = new ArrayList<>();

		newBathRooms.forEach(nb -> {
			String id = nb.getId();
			if(!bathRoomMap.containsKey(id)) {
				bathRoomMap.put(nb.getId(), nb);
				switch (nb.getAssignedGender()) {
					case MALE -> mb.add(nb);
					case FEMALE -> fb.add(nb);
					case NONE -> ub.add(nb);
				}
			}
		});
		maleWorker.addBathrooms(mb);
		femaleWorker.addBathrooms(fb);
		UNISEX_HANDLER.addBathrooms(ub);
	}

	public void removeBathrooms(List<Bathroom> removalBathRooms) {
		removalBathRooms.forEach(nb -> {
			String id = nb.getId();
			if(bathRoomMap.containsKey(id)) {
				bathRoomMap.remove(nb.getId());
			}
		});
	}

	public void accept(Person person) {
		if (person.getGender().equals(Gender.MALE)) maleWorker.accept(person);
		else femaleWorker.accept(person);
	}

	private static GenderWorker createWorker(Gender gender) {
		return new GenderWorker(
			new LinkedBlockingQueue<>(),
			ConcurrentHashMap.newKeySet(),
			UNISEX_HANDLER,
			gender
		);
	}
}

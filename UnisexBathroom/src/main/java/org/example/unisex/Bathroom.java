package org.example.unisex;

import java.util.HashSet;
import java.util.Set;

public interface Bathroom extends PersonStatusCallback {
	boolean canAccept(Gender gender);
	void addHandler(BathroomStatusCallback callback);
	Gender getAssignedGender();
	String getId();

	static Bathroom create(String id, Gender gender, int capacity) {
		return gender.equals(Gender.NONE) ? new UnisexBathroom(id, capacity) : new GenderedBathRoom(id, gender, capacity);
	}

	static Bathroom create(String id, int capacity) {
		return create(id, Gender.NONE, capacity);
	}

	final class DefaultBathroom implements Bathroom {
		private final String id;
		private final Gender gender;
		private final int capacity;
		private int occupancy;
		private final Set<Person> occupants;
		private BathroomStatusCallback callback;

		public DefaultBathroom(String id, Gender gender, int capacity) {
			this.id = id;
			this.gender = gender;
			this.capacity = capacity;
			this.occupancy = 0;
			this.occupants = new HashSet<>();
		}

		@Override
		public boolean canAccept(Gender gender) {
			return occupancy < capacity;
		}

		@Override
		public void addHandler(BathroomStatusCallback callback) {
			this.callback = callback;
		}

		@Override
		public void add(Person person) {
			occupants.add(person);
			occupancy++;
			person.assign(this);
			if(callback != null && occupancy == capacity) callback.occupied(id, this);
		}

		@Override
		public boolean remove(Person person) {
			occupants.remove(person);
			if(occupancy == capacity && callback != null) callback.open(id, this);
			occupancy--;
			return occupancy == 0;
		}

		@Override
		public Gender getAssignedGender() {
			return gender;
		}

		@Override
		public String getId() {
			return id;
		}
	}
}

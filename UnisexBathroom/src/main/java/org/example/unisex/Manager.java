package org.example.unisex;

import java.util.List;

public interface Manager {
	void addBathrooms(List<Bathroom> bathRooms);
	void removeBathrooms(List<Bathroom> bathRooms);
	void accept(Person person);
}

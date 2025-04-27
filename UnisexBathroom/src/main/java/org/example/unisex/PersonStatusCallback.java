package org.example.unisex;

public interface PersonStatusCallback {
	void add(Person person);
	// true indicates bathroom is empty
	boolean remove(Person person);
}

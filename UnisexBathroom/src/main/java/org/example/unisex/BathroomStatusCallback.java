package org.example.unisex;

public interface BathroomStatusCallback {
	void occupied(String id, Bathroom bathRoom);
	void open(String id, Bathroom bathRoom);
}

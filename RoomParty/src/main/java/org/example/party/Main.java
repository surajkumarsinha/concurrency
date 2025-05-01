package org.example.party;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		PartyRoom partyRoom = new PartyRoom(5);
		Thread.ofVirtual().start(new StudentGenerator(partyRoom, 500));
		Thread.ofVirtual().start(new Dean(partyRoom));
		Thread.sleep(20000);
	}
}
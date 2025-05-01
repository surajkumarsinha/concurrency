package org.example.party;

public class Dean implements Runnable {

	private final PartyRoom partyRoom;

	public Dean(PartyRoom partyRoom) {
		this.partyRoom = partyRoom;
	}

	@Override
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()) {
				partyRoom.getMutex().acquire(); // So that no operations can be performed by the students
				if(!partyRoom.canInspect()) {
					partyRoom.getMutex().release();
					partyRoom.setDeanState(DeanState.WAITING);
					partyRoom.getDeanEntry().acquire(); // wait for the entry
				}

				if(partyRoom.canBreakParty()) {
					enterRoom();
					breakupParty();
					partyRoom.getTurn().acquire(); // to stop students from coming inside the room
					partyRoom.getMutex().release(); // to signal students to leave
					partyRoom.getDeanExit().acquire();// To wait for all students to leave
					exitRoom();
					partyRoom.getTurn().release(); // now students can enter
				} else {
					searchRoom();
				}
				partyRoom.setDeanState(DeanState.NOT_AVAILABLE);
				partyRoom.getMutex().release();
				Thread.sleep(5000);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

	void enterRoom() {
		partyRoom.setDeanState(DeanState.IN_ROOM);
		System.out.println("\n<<<<    dean entered the room    >>>>" );
	}

	void exitRoom() {
		System.out.println("\n<<<<    dean entered the room    >>>> \n" );
	}

	void searchRoom() {
		System.out.println("\n<<<<    dean searching the room    >>>> \n" );
	}

	void breakupParty() {
		System.out.println("\n<<<<    dean broke the party    >>>> \n" );
	}
}

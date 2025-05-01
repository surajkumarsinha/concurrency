package org.example.party;

public class Student implements Runnable {
	private final int studentId;
	private final PartyRoom partyRoom;

	public Student(int studentId, PartyRoom partyRoom) {
		this.studentId = studentId;
		this.partyRoom = partyRoom;
	}

	@Override
	public void run() {
		try {
			partyRoom.getMutex().acquire();
			if(partyRoom.getDeanState() == DeanState.IN_ROOM) {
				partyRoom.getMutex().release();
				partyRoom.getTurn().acquire(); // wait for dean to leave
				partyRoom.getTurn().release();
				partyRoom.getMutex().acquire();
			}
			partyRoom.incrementStudents();
			if(partyRoom.canBreakParty() && partyRoom.getDeanState().equals(DeanState.WAITING)) {
				partyRoom.getDeanEntry().release();
			} else {
				partyRoom.getMutex().release();
			}
			party();
			Thread.sleep(1000);
			partyRoom.decrementStudents();
			System.out.println("<<<< Student " + studentId + " left the room  >>>");

			if(partyRoom.canInspect() && partyRoom.getDeanState().equals(DeanState.WAITING)) {
				partyRoom.getDeanEntry().release();
			} else if (partyRoom.canInspect() && partyRoom.getDeanState().equals(DeanState.IN_ROOM)) {
				partyRoom.getDeanExit().release();
			} else {
				partyRoom.getMutex().release();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	void party() {
		System.out.println("<<<<  Student: " + studentId + " is partying  >>>>");
	}
}

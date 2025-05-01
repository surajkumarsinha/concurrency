The following synchronization constraints apply to students and the Dean of Students:

Any number of students can be in a room at the same time.

The Dean of Students can only enter a room if there are no students in the room (to conduct a search) or if there are more than 50 students in the room (to break up the party).

While the Dean of Students is in the room, no additional students may enter, but students may leave.

The Dean of Students may not leave the room until all students have left.

There is only one Dean of Students, so you do not have to enforce exclusion among multiple deans.

Puzzle: write synchronization code for students and for the Dean of Students that enforces all of these constraints.
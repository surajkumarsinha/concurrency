package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

interface ReadWriteLock {
	void lockRead() throws InterruptedException;
	void lockWrite() throws InterruptedException;
	void unlockRead();
	void unlockWrite();

	final class WriterPriorityRWL implements ReadWriteLock {

		private int readers;
		private int writersInWaiting;
		private boolean isWriterActive;

		private final ReentrantLock lock;
		private final Condition canRead;
		private final Condition canWrite;

		public WriterPriorityRWL() {
			readers = 0;
			writersInWaiting = 0;
			isWriterActive = false;
			lock = new ReentrantLock();
			canRead = lock.newCondition();
			canWrite = lock.newCondition();
		}

		@Override
		public void lockRead() throws InterruptedException {
			lock.lock();
			try {
				while(isWriterActive || writersInWaiting > 0)
					canRead.await();
				readers++;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void lockWrite() throws InterruptedException {
			lock.lock();
			try {
				while(readers > 0 || isWriterActive) canWrite.await();
				writersInWaiting--;
				isWriterActive = true;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void unlockRead() {
			lock.lock();
			try {
				readers--;
				if(readers == 0) canWrite.signal();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void unlockWrite() {
			lock.lock();
			try {
				isWriterActive = false;
				if(writersInWaiting > 0) {
					canWrite.signal();
				} else {
					canRead.signalAll();
				}
			} finally {
				lock.unlock();
			}
		}
	}

	final class ReaderPriorityRWL implements ReadWriteLock {

		private int readers;
		private int writersInWaiting;
		private boolean isWriterActive;

		private final ReentrantLock lock;
		private final Condition canRead;
		private final Condition canWrite;

		public ReaderPriorityRWL() {
			readers = 0;
			writersInWaiting = 0;
			isWriterActive = false;
			lock = new ReentrantLock();
			canRead = lock.newCondition();
			canWrite = lock.newCondition();
		}

		@Override
		public void lockRead() throws InterruptedException {
			lock.lock();
			try {
				while(isWriterActive)
					canRead.await();
				readers++;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void lockWrite() throws InterruptedException {
			lock.lock();
			try {
				while(readers > 0 || isWriterActive) canWrite.await();
				writersInWaiting--;
				isWriterActive = true;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void unlockRead() {
			lock.lock();
			try {
				readers--;
				if(readers == 0) canWrite.signal();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void unlockWrite() {
			lock.lock();
			try {
				isWriterActive = false;
				canRead.signalAll();
				canWrite.signal();
			} finally {
				lock.unlock();
			}
		}
	}
}
package org.example;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface DistributedLock {
	String lock(String key);
	boolean unlock(String clientId, String key);
	void renewLease(String clientId, String key);

	final class DistributedLockImpl implements DistributedLock {
		record LockEntry(String ownerId, long expiryTime) {}

		private final ConcurrentHashMap<String, LockEntry> locks = new ConcurrentHashMap<>();
		private final long leaseMillis = 10000;

		@Override
		public String lock(String key) {
			String ownerId = UUID.randomUUID().toString();
			long now = System.currentTimeMillis();

			LockEntry newEntry = new LockEntry(ownerId, now + leaseMillis);
			LockEntry result = locks.compute(key, (k, existing) -> {
				if (existing == null)
					return newEntry;

				if (existing.expiryTime < now)
					return newEntry;

				return existing;
			});

			if (result == newEntry)
				return ownerId;

			return null;
		}

		@Override
		public boolean unlock(String ownerId, String key) {
			return locks.computeIfPresent(key, (k, existing) -> {

				if (!existing.ownerId.equals(ownerId))
					return existing;

				return null;

			}) == null;
		}

		@Override
		public void renewLease(String clientId, String key) {

		}
	}
}

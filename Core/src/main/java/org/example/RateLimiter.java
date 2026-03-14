package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimiter {
	private final Map<String, Limiter> clientIdVsLimiters;
	private final Map<String, Lock> clientIdVsLock;

	public RateLimiter() {
		this.clientIdVsLimiters = new ConcurrentHashMap<>();
		this.clientIdVsLock = new ConcurrentHashMap<>();
	}

	RateLimitResult allow(String clientId, String key) {
		Lock lock = clientIdVsLock.get(clientId);
		if(lock == null) throw new RuntimeException("Client not registered");
		return clientIdVsLimiters.get(clientId).allow(key);
	}

	void addLimiter(String clientId, Limiter limiter) {
		clientIdVsLock.compute(clientId, (key, lock) -> {
			if(lock == null) {
				clientIdVsLimiters.put(clientId, limiter);
				clientIdVsLock.put(clientId, new ReentrantLock());
			} else {
				lock.lock();
				try {
					clientIdVsLimiters.put(clientId, limiter);
				} finally {
					lock.unlock();
				}
			}
			return lock;
		});


	}

}

record RateLimitResult(boolean allowed, int remaining, long retryAfterInMillis) {}

interface Limiter {
	RateLimitResult allow(String key);

	final class TokenBucketLimiter implements Limiter {

		static class TokenBucket {
			double token;
			long lastRefillTime;

			public TokenBucket(double token, long lastRefillTime) {
				this.token = token;
				this.lastRefillTime = lastRefillTime;
			}
		}

		private final Map<String, TokenBucket> keyVsTokenBucket;
		private final int refillRatePerSecond;
		private final int numOfTokens;

		public TokenBucketLimiter(int refillRatePerSecond, int numOfTokens) {
			this.keyVsTokenBucket = new ConcurrentHashMap<>();
			this.refillRatePerSecond = refillRatePerSecond;
			this.numOfTokens = numOfTokens;
		}

		@Override
		public RateLimitResult allow(String key) {
			long currentTimeInMillis = System.currentTimeMillis();
			TokenBucket tokenBucket = getOrCreateBucket(key, currentTimeInMillis);

			long elapsedTimeInMillis = currentTimeInMillis - tokenBucket.lastRefillTime;
			double tokensAdded = (elapsedTimeInMillis * refillRatePerSecond) / 1000.0;
			tokenBucket.token = Math.min(numOfTokens, tokenBucket.token + tokensAdded);
			tokenBucket.lastRefillTime = currentTimeInMillis;

			if(tokenBucket.token >= 1) {
				tokenBucket.token -= 1.0;
				return new RateLimitResult(true, (int) tokenBucket.token, 0);
			} else {
				long retryTime = (long)(1.0 - tokenBucket.token) * 1000 / refillRatePerSecond;
				return new RateLimitResult(false, 0, retryTime);
			}

		}

		private TokenBucket getOrCreateBucket(String key, long currentTimeInMillis) {
			return keyVsTokenBucket.compute(key, (k, tokenBucket) -> {
				if(tokenBucket == null) {
					return new TokenBucket(numOfTokens, currentTimeInMillis);
				} else {
					return tokenBucket;
				}
			});
		}
	}
}



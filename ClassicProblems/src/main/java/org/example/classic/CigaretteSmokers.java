package org.example.classic;

import java.util.List;
import java.util.concurrent.Semaphore;

public class CigaretteSmokers {
	private final Semaphore agentSem = new Semaphore(1);
	private final Semaphore tobacco = new Semaphore(0);
	private final Semaphore match = new Semaphore(0);
	private final Semaphore paper = new Semaphore(0);
	private final List<Semaphore> sems = List.of(tobacco, match, paper);
	// There will be 3 concurrent agent threads which will provide each ingredient

	public static void main(String[] args) {
		Table table = new Table();
	}

	final class Agent implements Runnable {
		private Semaphore ingredientA;
		private Semaphore ingredientB;
		private final int id;

		public Agent(int id) {
			this.id = id;
			for(int i=0; i<3; i++) {
				if(id == i) continue;
				if(ingredientA == null) ingredientA = sems.get(i);
				else ingredientB = sems.get(i);
			}
		}

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					agentSem.acquire();
					ingredientA.release();
					ingredientB.release();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	final class Smoker implements Runnable {
		private final int id;
		private Semaphore ingredient;
		public Smoker(int id) {
			this.id = id;
			ingredient = sems.get(id);
		}

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					ingredient.acquire();
					agentSem.release();
					smoke();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

			}
		}

		void smoke() throws InterruptedException {
			System.out.println("Smoker-" + id + " is smoking");
			Thread.sleep(2000);
		}
	}

	static final class Pusher implements Runnable {


		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {

			}
		}
	}

	static final class Table {
		private boolean paperAvailable;
		private boolean tobaccoAvailable;
		private boolean matchAvailable;

		public Table() {
			paperAvailable = false;
			tobaccoAvailable = false;
			matchAvailable = false;
		}

		public boolean isPaperAvailable() {
			return paperAvailable;
		}

		public void setPaperAvailable(boolean paperAvailable) {
			this.paperAvailable = paperAvailable;
		}

		public boolean isTobaccoAvailable() {
			return tobaccoAvailable;
		}

		public void setTobaccoAvailable(boolean tobaccoAvailable) {
			this.tobaccoAvailable = tobaccoAvailable;
		}

		public boolean isMatchAvailable() {
			return matchAvailable;
		}

		public void setMatchAvailable(boolean matchAvailable) {
			this.matchAvailable = matchAvailable;
		}
	}
}

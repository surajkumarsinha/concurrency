package org.example.unisex;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	public static void main(String[] args) {

		try(ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
			List<Bathroom> bathRooms = List.of(
				Bathroom.create("1", 5),
				Bathroom.create("2", Gender.MALE, 3),
				Bathroom.create("3", Gender.NONE, 3),
				Bathroom.create("4", Gender.FEMALE, 5),
				Bathroom.create("5", Gender.MALE, 2)
			);

			Manager bathroomManager = new DefaultManager();
			bathroomManager.addBathrooms(bathRooms);

			for (int i = 1; i <= 50; i++) {
				Random rand = new Random();
				Gender gender = rand.nextBoolean() ? Gender.MALE : Gender.FEMALE;
				Thread.sleep(rand.nextLong(500, 5000));
				Person person = new Person("person" + i, gender);
				executorService.submit(person);
				bathroomManager.accept(person);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
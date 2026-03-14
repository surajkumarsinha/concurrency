package org.example.topo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TopoSort {
	private final ExecutorService executor;

	public TopoSort() {
		this.executor = Executors.newFixedThreadPool(3);
	}

	public void sort(List<List<Integer>> dependencies, int n) throws InterruptedException {
		List<List<Integer>> graph = new ArrayList<>();
		AtomicInteger[] indegree = new AtomicInteger[n + 1];

		for (int i = 0; i <= n; i++) {
			graph.add(new ArrayList<>());
			indegree[i] = new AtomicInteger(0);
		}

		for (int sql = 1; sql <= n; sql++) {
			for (int prereq : dependencies.get(sql)) {
				graph.get(prereq).add(sql);
				indegree[sql].incrementAndGet();
			}
		}

		Queue<Integer> queue = new LinkedList<>();


	}
}

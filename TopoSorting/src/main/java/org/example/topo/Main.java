package org.example.topo;

import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		List<List<Integer>> dependencies = initialiseDeps();
	}

	private static List<List<Integer>> initialiseDeps() {
		int n = 20;

// deps[i] contains list of SQLs that must execute BEFORE SQL i
		List<List<Integer>> deps = new ArrayList<>();
		for (int i = 0; i <= n; i++) {
			deps.add(new ArrayList<>());
		}

// Level 0 (no dependencies) : 1,2,3,4
		deps.get(1).addAll(List.of());
		deps.get(2).addAll(List.of());
		deps.get(3).addAll(List.of());
		deps.get(4).addAll(List.of());

// Level 1
		deps.get(5).addAll(List.of(1));       // 5 depends on 1
		deps.get(6).addAll(List.of(2));       // 6 depends on 2
		deps.get(7).addAll(List.of(4));       // 7 depends on 4

// Level 2 (combined dependencies)
		deps.get(8).addAll(List.of(5, 6));    // 8 depends on 5, 6
		deps.get(9).addAll(List.of(8));       // 9 depends on 8
		deps.get(10).addAll(List.of(3, 6));   // 10 depends on 3, 6

// Level 3
		deps.get(11).addAll(List.of(9));      // 11 depends on 9
		deps.get(12).addAll(List.of(10));     // 12 depends on 10

// Level 4
		deps.get(13).addAll(List.of(11));     // 13 depends on 11
		deps.get(14).addAll(List.of(12));     // 14 depends on 12

// Level 5 (join)
		deps.get(15).addAll(List.of(13, 14)); // 15 depends on 13 and 14

// Level 6
		deps.get(16).addAll(List.of(15));     // 16 depends on 15

// Level 7 (branch)
		deps.get(17).addAll(List.of(16));     // 17 depends on 16
		deps.get(18).addAll(List.of(16));     // 18 depends on 16

// Level 8 (merge)
		deps.get(19).addAll(List.of(17, 18)); // 19 depends on 17 and 18

// Final
		deps.get(20).addAll(List.of(19));     // 20 depends on 19

		return deps;
	}
}
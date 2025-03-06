package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.core.evaluator.Pattern;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.IntStream;

public class LineImplTest implements WithAssertions {

	@ParameterizedTest
	@EnumSource(value = Stone.class, names = {"WHITE", "BLACK"})
	void testTerminalCalculation(Stone stone) {
		var moves = new LinkedList<Entry<Integer, Stone>>();
		IntStream.range(0, Pattern.LENGTH).forEach(index -> moves.add(new SimpleImmutableEntry<>(index, stone)));
		for (var permutation : generatePermutations(moves)) {
			var line = new LineImpl(Pattern.LENGTH);
			for (var move : permutation) {
				assertThat(line.isTerminal()).isFalse();
				line = line.withStone(move.getKey(), move.getValue());
			}
			assertThat(line.isTerminal()).isTrue();
		}
	}

	@ParameterizedTest
	@CsvSource({
			"ooooo",
			"-ooooo", "ooooo-", "-ooooo-",
			"xooooo", "ooooox", "xooooox",
			"#ooooo", "ooooo#", "#ooooo#"
	})
	void testBeforeAndAfterCalculation(String stones) {
		var baseLine = new LineImpl(stones.length());
		var moves = new LinkedList<Entry<Integer, Stone>>();
		for (int index = 0; index < stones.length(); ++index) {
			var stone = Stone.valueOf(stones.charAt(index));
			switch (stone) {
				case WHITE -> moves.add(new SimpleImmutableEntry<>(index, Stone.WHITE));
				case BLACK -> baseLine = baseLine.withStone(index, Stone.BLACK);
				case BLOCK -> baseLine = baseLine.withStone(index, Stone.BLOCK);
			}
		}
		for (var permutation : generatePermutations(moves)) {
			var line = baseLine;
			for (var move : permutation) {
				assertThat(line.isTerminal()).as("Before move %s", move.getKey().toString()).isFalse();
				line = line.withStone(move.getKey(), move.getValue());
			}
			assertThat(line.isTerminal()).isTrue();
		}
	}

	public <T> List<List<T>> generatePermutations(List<T> list) {
		if (list.size() <= 1)
			return List.of(list);
		List<List<T>> result = new LinkedList<>();
		for (int index = 0; index < list.size() - 1; ++index) {
			var item = list.get(index);
			var subList = new ArrayList<T>(list.size() - 1);
			for (int subIndex = 0; subIndex < list.size(); ++subIndex)
				if (subIndex != index)
					subList.add(list.get(subIndex));
			for (var subPermutation : generatePermutations(subList)) {
				var permutation = new ArrayList<T>(list.size());
				permutation.add(item);
				permutation.addAll(subPermutation);
				result.add(permutation);
			}
		}
		return result;
	}

}

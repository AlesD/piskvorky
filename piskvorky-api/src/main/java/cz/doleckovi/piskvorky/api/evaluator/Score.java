package cz.doleckovi.piskvorky.api.evaluator;

public interface Score extends Comparable<Score> {

	int getValue();
	Score invert();

	default Score min(Score other) {
		if (compareTo(other) > 0)
			return other;
		return this;
	}

	default Score max(Score other) {
		if (compareTo(other) < 0)
			return other;
		return this;
	}

}

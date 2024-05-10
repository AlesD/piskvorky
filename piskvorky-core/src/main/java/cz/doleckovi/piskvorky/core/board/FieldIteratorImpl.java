package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Field;
import cz.doleckovi.piskvorky.api.board.FieldIterator;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Iterator for navigation over fields of position. */
final class FieldIteratorImpl<T extends Field> implements FieldIterator<T> {

	private final T[] fields;
	private final int start;
	private final int end;
	private final int step;
	private int previousIndex;
	private int nextIndex;

	public FieldIteratorImpl(T[] fields, int start, int end, int step, int previousIndex) {
		this.fields = fields;
		this.start = start;
		this.end = end;
		this.step = step;
		this.previousIndex = previousIndex;
		this.nextIndex = previousIndex + step;
	}

	@Override
	public FieldIterator<T> reverseIterator() {
		return new FieldIteratorImpl(fields, end, start, -step, previousIndex);
	}

	@Override
	public boolean hasNext() {
		return previousIndex != end;
	}

	@Override
	public int nextIndex() {
		return nextIndex;
	}

	@Override
	public T next() {
		if (previousIndex == end)
			throw new NoSuchElementException();
		previousIndex = nextIndex;
		nextIndex += step;
		return fields[previousIndex];
	}

	@Override
	public boolean hasPrevious() {
		return nextIndex != start;
	}

	@Override
	public int previousIndex() {
		return previousIndex;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove");
	}

	@Override
	public void set(T field) {
		throw new UnsupportedOperationException("set");
	}

	@Override
	public void add(T field) {
		throw new UnsupportedOperationException("add");
	}

	@Override
	public T previous() {
		if (nextIndex == start)
			throw new NoSuchElementException();
		nextIndex = previousIndex;
		previousIndex -= step;
		return fields[nextIndex];
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FieldIteratorImpl that = (FieldIteratorImpl) o;
		return start == that.start && end == that.end && step == that.step && previousIndex == that.previousIndex && Arrays.equals(fields, that.fields);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(start, end, step, previousIndex);
		result = 31 * result + Arrays.hashCode(fields);
		return result;
	}

	@Override
	public String toString() {
		return "<" + start + ',' + nextIndex + ',' + end + '>';
	}

}

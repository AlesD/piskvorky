package cz.doleckovi.piskvorky.gtp.parser;

import org.antlr.v4.runtime.IntStream;

import java.util.function.IntSupplier;

import static java.util.Objects.requireNonNull;

public class NonBlockingIntStream implements IntStream {

	private final String sourceName;
	private final IntSupplier supplier;

	// Buffer size should be power of 2
	protected int[] buffer = new int[2];
	protected int mask = buffer.length - 1;

	// These are pointers to stream
	protected int tailIndex; // Lowest index still in buffer
	protected int index;     // Current index
	protected int nextIndex; // Next Index to read
	private int markIndex; // Lowest mark - can't be overwritten
	private int eofIndex = Integer.MAX_VALUE; // EOF index

	private int markCount;

	public NonBlockingIntStream(String sourceName, IntSupplier supplier) {
		this.sourceName = requireNonNull(sourceName, "Parameter sourceName is null");
		this.supplier = requireNonNull(supplier, "Parameter supplier is null");
	}

	public NonBlockingIntStream(IntSupplier supplier) {
		this(UNKNOWN_SOURCE_NAME, supplier);
	}

	@Override
	public String getSourceName() {
		return sourceName;
	}

	@Override
	public int size() {
		if (eofIndex != Integer.MAX_VALUE)
			return eofIndex;
		throw new UnsupportedOperationException("Size is not known");
	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public void seek(int index) throws IllegalArgumentException, UnsupportedOperationException {
		if (index < 0)
			throw new IllegalArgumentException("Negative index");
		if (index < tailIndex)
			throw new UnsupportedOperationException("Index %d if no longer in buffer".formatted(index));
		if (index > nextIndex && !fillToIndex(index - 1))
			index = eofIndex;
		this.index = index;
	}

	@Override
	public int mark() {
		if (markCount == 0) {
			markIndex = index;
		} else if (index < markIndex) {
			throw new IllegalArgumentException("Mark can't be less than %d".formatted(markIndex));
		}
		return ++markCount;
	}

	@Override
	public void release(int marker) {
		if (markCount == 0)
			throw new IllegalStateException("No mark to release");
		if (marker != markCount)
			throw new IllegalArgumentException("Can't release marker %d - expecting release of marker %d"
					.formatted(marker, markCount));
		--markCount;
	}

	@Override
	public void consume() {
		if (index == nextIndex)
			throw new IllegalStateException("Buffer is empty");
		++index;
	}

	@Override
	public int LA(int offset) {
		if (offset < 0) {
			var readIndex = index + offset;
			if (readIndex < tailIndex)
				throw new UnsupportedOperationException("Can't index %d lower than tail %d".formatted(readIndex, tailIndex));
			return buffer[readIndex & mask];
		}
		if (offset == 0)
			throw new UnsupportedOperationException("Can't provide LA(0)");
		var readIndex = index + offset - 1;
		if (readIndex >= eofIndex)
			return EOF;
		if (readIndex >= nextIndex && !fillToIndex(readIndex))
			return EOF;
		return buffer[readIndex & mask];
	}

	/** Fill the buffer up to given index.
	 * @param targetIndex Index of stream to obtain
	 * @return {@code true} if the buffer was filled, {@code false} if EOF was hit
	 */
	private boolean fillToIndex(int targetIndex) {
		while (targetIndex >= nextIndex) {
			int value = supplier.getAsInt();
			if (value == -1) {
				eofIndex = nextIndex;
				return false;
			}
			var nextOffset = nextIndex & mask;
			if (markCount > 0 && nextOffset == (markIndex & mask) && nextIndex != markIndex) {
				expandBuffer();
				nextOffset = nextIndex & mask;
			}
			buffer[nextOffset] = value;
			if (nextOffset == (tailIndex & mask) && nextIndex != tailIndex)
				++tailIndex;
			++nextIndex;
		}
		return true;
	}

	private void expandBuffer() {
		var newBuffer = new int[buffer.length << 1];
		var newMask = newBuffer.length - 1;
		var offset = markIndex & mask;
		var length = buffer.length - offset;
		System.arraycopy(buffer, offset, newBuffer, markIndex & newMask, length);
		if (offset > 0)
			System.arraycopy(buffer, 0, newBuffer, (markIndex + length) & newMask ,offset);
		buffer = newBuffer;
		mask = newMask;
	}

}


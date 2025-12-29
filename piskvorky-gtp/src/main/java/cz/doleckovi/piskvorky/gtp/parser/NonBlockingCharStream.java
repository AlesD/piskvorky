package cz.doleckovi.piskvorky.gtp.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.misc.Interval;

import java.util.function.IntSupplier;

public class NonBlockingCharStream extends NonBlockingIntStream implements CharStream {

	public NonBlockingCharStream(String sourceName, IntSupplier supplier) {
		super(sourceName, supplier);
	}

	public NonBlockingCharStream(IntSupplier supplier) {
		super(supplier);
	}

	@Override
	public String getText(Interval interval) {
		if (interval.b < interval.a)
			return "";
		var fromOffset = interval.a & mask;
		var toOffset = interval.b & mask;
		if (fromOffset < toOffset)
			return new String(buffer, fromOffset, toOffset - fromOffset + 1);
		var atEnd = new String(buffer, fromOffset, buffer.length - fromOffset);
		var atStart = new String(buffer, 0, toOffset + 1);
		return atEnd + atStart;
	}

}

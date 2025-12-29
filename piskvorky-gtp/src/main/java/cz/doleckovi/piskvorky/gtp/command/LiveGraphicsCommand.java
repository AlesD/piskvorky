package cz.doleckovi.piskvorky.gtp.command;

import java.io.PrintWriter;

public interface LiveGraphicsCommand {

	ScopedValue<PrintWriter> OUTPUT = ScopedValue.newInstance();

	default PrintWriter getOutput() {
		return OUTPUT.get();
	}

}

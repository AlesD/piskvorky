package cz.doleckovi.piskvorky.gtp;

import java.io.PrintStream;
import java.io.PrintWriter;

public interface CommandContext {

	Session session();
    PrintWriter stdout();
	PrintWriter stderr();

}

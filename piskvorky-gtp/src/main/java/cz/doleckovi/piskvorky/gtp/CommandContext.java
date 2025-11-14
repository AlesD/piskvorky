package cz.doleckovi.piskvorky.gtp;

import java.io.PrintWriter;
import java.io.Writer;

public interface CommandContext {

	Session session();
	PrintWriter stdout();
	PrintWriter stderr();

}

package cz.doleckovi.piskvorky.gtp.parser;

import cz.doleckovi.piskvorky.gtp.autoconfigure.LineSupplier;

import java.util.Scanner;

public class ScannerLineSupplier implements LineSupplier {

	private final Scanner scanner;

	public ScannerLineSupplier(Scanner scanner) {
		this.scanner = scanner;
	}


	@Override
	public String getLine() {
		if (scanner.hasNextLine())
			return scanner.nextLine();
		return null;
	}

}

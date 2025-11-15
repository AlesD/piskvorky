package cz.doleckovi.piskvorky.gtp.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.Scanner;

@AutoConfiguration
public class InputAutoConfiguration {

	@ConditionalOnMissingBean(Scanner.class)
	Scanner stdinScanner() {
		Scanner result;
		if (System.console() != null) {
			return new Scanner(System.console().reader());
		}
		return new Scanner(System.in, System.getProperty("stdin.encoding"));
	}

	Scanner fileScanner() {

	}

}

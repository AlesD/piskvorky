package cz.doleckovi.piskvorky.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/** Application context tests.
 * <p>The application boots with the default profile. The {@code gtp} profile is
 * deliberately excluded because its {@link GtpLoopRunner} binds STDIN as a GTP
 * conversation — exercised end-to-end in {@code piskvorky-gtp}
 * {@code SessionTest} instead.</p>
 */
@SpringBootTest(properties = "spring.profiles.active=default")
class PiskvorkyApplicationTests {

	@Test
	void contextLoads() {
	}

}

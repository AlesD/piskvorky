package cz.doleckovi.piskvorky.core.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;

@AutoConfiguration
@ConditionalOnBooleanProperty(value = "cz.piskvorky.core.enabled", matchIfMissing = true)
public class CoreAutoConfiguration {

    public class CoreConfiguration {

    }

}

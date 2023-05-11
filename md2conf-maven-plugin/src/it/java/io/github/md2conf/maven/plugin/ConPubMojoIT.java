package io.github.md2conf.maven.plugin;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConPubMojoIT extends AbstractMd2ConfMojoIT {

    @Test
    void skip() {
        // arrange
        Map<String, String> properties = mandatoryProperties();
        properties.put("skip", "true");
        // act
        var res = invokeGoalAndVerify("conpub", "default", properties);
        Path outputPath = res.toPath().resolve("target/md2conf");
        assertThat(outputPath).doesNotExist();
    }

    private static Map<String, String> mandatoryProperties() {
        Map<String, String> properties = new HashMap<>();
        return properties;
    }

}
package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.ConfluenceClientConfigurationProperties.ConfluenceClientConfigurationPropertiesBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.github.md2conf.confluence.client.ConfluenceClientConfigurationProperties.ConfluenceClientConfigurationPropertiesBuilder.aConfluenceClientConfigurationProperties;

@Testcontainers
public class AbstractContainerTestBase {

    @Container
    public static GenericContainer confluence = new GenericContainer(DockerImageName.parse("qwazer/atlassian-sdk-confluence:latest"))
            .withExposedPorts(8090)
            .waitingFor(Wait.forHttp("/")
                            .forStatusCode(200)
                            .forStatusCode(302)
                            .withStartupTimeout(Duration.ofMinutes(10)));

    String confluenceBaseUrl(){
        return String.format("http://localhost:%s", confluence.getFirstMappedPort());
    }

    static String PARENT_PAGE_TITLE = "Welcome to Confluence";
    static String SPACE_KEY = "ds";

    ConfluenceClientConfigurationPropertiesBuilder aDefaultConfluenceClientConfigurationProperties(){
        return aConfluenceClientConfigurationProperties()
                .withConfluenceUrl(confluenceBaseUrl())
                .withUsername("admin")
                .withPasswordOrPersonalAccessToken("admin")
                .withSpaceKey(SPACE_KEY)
                .withParentPageTitle(PARENT_PAGE_TITLE);
    }


}

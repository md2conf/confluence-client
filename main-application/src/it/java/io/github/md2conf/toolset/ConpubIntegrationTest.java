package io.github.md2conf.toolset;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ConpubIntegrationTest extends AbstractContainerTestBase{

    @TempDir
    private Path outputPath;

    @Test
    public void convert_and_publish() {
        assertThat(super.pageIdBy(PARENT_PAGE_TITLE)).isNullOrEmpty();
        deletePageIfExists("Sample");
        deletePageIfExists("Appendix 01");

        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        String inputPath = "src/it/resources/several-pages";
        String[] args = ArrayUtils.addAll(commonConvertAndPublishArgs(),
                "-i", inputPath,
                "-o", outputPath.toString());
        int exitCode = cmd.execute(args);
        assertThat(exitCode).isEqualTo(0);

        // check sample page
        String id = super.pageIdBy("Sample");
        assertThat(id).isNotNull();
        assertThat(pageBodyStorageById(id)).doesNotContain("<h1>Sample</h1>"); //first header removed from content and becomes the title
        assertThat(pageBodyStorageById(id)).contains("<ac:image><ri:attachment ri:filename=\"sample.gif\" /></ac:image>");
        assertThat(pageAttachmentsTitles(id)).contains("attachment.txt", "sample.gif" ).hasSize(2);

        // check appendix page
        String appendixId = super.pageIdBy("Appendix 01");
        assertThat(appendixId).isNotNull();
        assertThat(pageBodyStorageById(appendixId)).doesNotContain("<h1>Appendix 01</h1>"); //first header removed from content and becomes the title
        assertThat(pageAttachmentsTitles(appendixId)).isEmpty();

    }

    private String[] commonConvertAndPublishArgs() {
        String[] args = new String[]{"conpub"};
        args = ArrayUtils.addAll(args, CLI_OPTIONS);
        args = ArrayUtils.addAll(args, "-url", confluenceBaseUrl());
        return args;
    }
}

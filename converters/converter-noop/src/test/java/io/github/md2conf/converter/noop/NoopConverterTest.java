package io.github.md2conf.converter.noop;

import io.github.md2conf.converter.ConfluencePageFactory;
import io.github.md2conf.converter.Converter;
import io.github.md2conf.converter.ExtractTitleStrategy;
import io.github.md2conf.indexer.DefaultFileIndexer;
import io.github.md2conf.indexer.FileIndexer;
import io.github.md2conf.indexer.FileIndexerConfigurationProperties;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class NoopConverterTest {

    ConfluencePageFactory confluencePageFactory = new ConfluencePageFactory(ExtractTitleStrategy.FROM_FILENAME);
    FileIndexer fileIndexer = new DefaultFileIndexer(new FileIndexerConfigurationProperties());
    Converter converter = new NoopConverter(confluencePageFactory);

    @Test
    void convert_dir_with_attachments() throws IOException {
        String path = "src/test/resources/dir_with_attachments";
        File f = new File(path);
        PagesStructure structure = fileIndexer.indexPath(f.toPath());
        Assertions.assertThat(structure.pages()).hasSize(1);
        ConfluenceContentModel model = converter.convert(structure);
        Assertions.assertThat(model).isNotNull();
        Assertions.assertThat(model.getPages().get(0)).isNotNull();
        Assertions.assertThat(model.getPages().get(0).getAttachments()).hasSize(2);
        Assertions.assertThat(model.getPages().get(0).getAttachments().keySet()).contains("1");
        Assertions.assertThat(model.getPages().get(0).getAttachments().keySet()).contains("attach");
        Assertions.assertThat(model.getPages().get(0).getChildren()).hasSize(1);
    }
}
package io.github.md2conf.converter.copying;

import io.github.md2conf.converter.AttachmentUtil;
import io.github.md2conf.converter.Converter;
import io.github.md2conf.indexer.Page;
import io.github.md2conf.indexer.PagesStructure;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;
import io.github.md2conf.title.processor.PageStructureTitleProcessor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CopyingConverter implements Converter {

    private final PageStructureTitleProcessor pagesStructureTitleProcessor;
    private final Path outputPath;


    public CopyingConverter(PageStructureTitleProcessor pagesStructureTitleProcessor, Path outputPath) {
        this.pagesStructureTitleProcessor = pagesStructureTitleProcessor;
        this.outputPath = outputPath;
    }

    @Override
    public ConfluenceContentModel convert(PagesStructure pagesStructure) throws IOException {
        Map<Path,String> titleMap = pagesStructureTitleProcessor.toTitleMap(pagesStructure);
        List<ConfluencePage> confluencePages = new ArrayList<>();
        for (Page topLevelPage : pagesStructure.pages()) { //use "for" loop to throw exception to caller
            ConfluencePage confluencePage;
            confluencePage = copyAndCreateConfluencePage(topLevelPage, Paths.get(""), titleMap);
            confluencePages.add(confluencePage);
        }
        return new ConfluenceContentModel(confluencePages);
    }

    private ConfluencePage copyAndCreateConfluencePage(Page page, Path relativePart, Map<Path, String> titleMap) throws IOException {
        //copy
        Path targetPath = PathUtils.copyFileToDirectory(page.path(), outputPath.resolve(relativePart));
        List<Path> copiedAttachments = AttachmentUtil.copyPageAttachments(targetPath, page.attachments());
        // create ConfluencePage
        ConfluencePage result = new ConfluencePage();
        result.setContentFilePath(targetPath.toString());
        result.setTitle(titleMap.get(page.path().toAbsolutePath()));
        result.setAttachments(AttachmentUtil.toAttachmentsMap(copiedAttachments));
        if (page.children() != null && !page.children().isEmpty()) {
            String childrenDirAsStr = FilenameUtils.concat(
                    relativePart.toString(),
                    FilenameUtils.removeExtension(targetPath.getFileName().toString()));
            Path childrenDir = outputPath.resolve(childrenDirAsStr);
            if (!childrenDir.toFile().mkdirs()) {
                throw new IOException("Cannot create dirs in " + childrenDir);
            }
            for (Page childPage : page.children()) {
                result.getChildren().add(copyAndCreateConfluencePage(childPage, outputPath.relativize(childrenDir), titleMap));
            }
        }
        return result;
    }

}

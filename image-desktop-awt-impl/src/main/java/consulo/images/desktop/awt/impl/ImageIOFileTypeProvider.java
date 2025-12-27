package consulo.images.desktop.awt.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.BinaryImageFileType;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;
import jakarta.annotation.Nonnull;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
@ExtensionImpl
public class ImageIOFileTypeProvider extends FileTypeFactory {
    @Override
    public void createFileTypes(@Nonnull FileTypeConsumer fileTypeConsumer) {
        Set<String> processed = new HashSet<>();

        for (String format : ImageIOProxy.getReaderFormatNames()) {
            String ext = format.toLowerCase(Locale.ROOT);
            processed.add(ext);
        }

        fileTypeConsumer.consume(BinaryImageFileType.INSTANCE, StringUtil.join(processed, FileTypeConsumer.EXTENSION_DELIMITER));
    }
}

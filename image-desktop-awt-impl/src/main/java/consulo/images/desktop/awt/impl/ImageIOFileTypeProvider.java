package consulo.images.desktop.awt.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.BinaryImageFileType;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;

import jakarta.annotation.Nonnull;

import javax.imageio.ImageIO;
import java.util.HashSet;
import java.util.Set;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
@ExtensionImpl
public class ImageIOFileTypeProvider extends FileTypeFactory {
    @Override
    public void createFileTypes(@Nonnull FileTypeConsumer fileTypeConsumer) {
        ImageIO.scanForPlugins();

        final Set<String> processed = new HashSet<>();

        final String[] readerFormatNames = ImageIO.getReaderFormatNames();
        for (String format : readerFormatNames) {
            final String ext = format.toLowerCase();
            processed.add(ext);
        }

        processed.add(IfsUtil.ICO_FORMAT.toLowerCase());

        fileTypeConsumer.consume(BinaryImageFileType.INSTANCE, StringUtil.join(processed, FileTypeConsumer.EXTENSION_DELIMITER));
    }
}

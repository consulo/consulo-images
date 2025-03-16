package consulo.images.svg.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.svg.SVGFileType;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2018-08-07
 */
@ExtensionImpl
public class SVGImageFileTypeProvider extends FileTypeFactory {
    @Override
    public void createFileTypes(@Nonnull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(SVGFileType.INSTANCE);
    }
}

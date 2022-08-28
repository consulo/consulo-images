package consulo.images.unified.file.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.BinaryImageFileType;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;

import javax.annotation.Nonnull;

/**
 * This factory used for registering known image file types. AWT implementation didn't use this class
 *
 * @author VISTALL
 * @see consulo.images.desktop.awt.impl.ImageIOFileTypeProvider
 * @since 26-Aug-22
 */
@ExtensionImpl
public class UnifiedImageFileTypeFactory extends FileTypeFactory {
  private static final String KNOWN_EXTENSIONS = "png;gif;jpg;jpeg;bmp;ico;tif;tiff";

  @Override
  public void createFileTypes(@Nonnull FileTypeConsumer fileTypeConsumer) {
    fileTypeConsumer.consume(BinaryImageFileType.INSTANCE, KNOWN_EXTENSIONS);
  }
}

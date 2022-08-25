package consulo.images.svg;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.ImageFileTypeProvider;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import org.intellij.images.fileTypes.impl.SvgFileType;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2018-08-07
 */
@ExtensionImpl
public class SVGImageFileTypeProvider implements ImageFileTypeProvider {
  @Override
  public void register(@Nonnull FileTypeConsumer fileTypeConsumer) {
    fileTypeConsumer.consume(SvgFileType.INSTANCE);
  }
}

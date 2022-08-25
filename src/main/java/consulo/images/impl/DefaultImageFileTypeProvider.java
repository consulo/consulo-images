package consulo.images.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.ImageFileType;
import consulo.images.ImageFileTypeProvider;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import org.intellij.images.vfs.IfsUtil;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.util.HashSet;
import java.util.Set;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
@ExtensionImpl
public class DefaultImageFileTypeProvider implements ImageFileTypeProvider {
  @Override
  public void register(@Nonnull FileTypeConsumer fileTypeConsumer) {
    final Set<String> processed = new HashSet<>();

    final String[] readerFormatNames = ImageIO.getReaderFormatNames();
    for (String format : readerFormatNames) {
      final String ext = format.toLowerCase();
      processed.add(ext);
    }

    processed.add(IfsUtil.ICO_FORMAT.toLowerCase());

    fileTypeConsumer.consume(ImageFileType.INSTANCE, StringUtil.join(processed, FileTypeConsumer.EXTENSION_DELIMITER));
  }
}

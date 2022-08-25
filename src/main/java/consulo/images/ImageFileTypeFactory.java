package consulo.images;

import consulo.annotation.component.ExtensionImpl;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;
import consulo.virtualFileSystem.fileType.FileTypeFactory;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.intellij.images.fileTypes.impl.ImageFileTypeManagerImpl;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
@ExtensionImpl
public class ImageFileTypeFactory extends FileTypeFactory {
  @Override
  public void createFileTypes(@Nonnull FileTypeConsumer consumer) {
    ImageFileTypeManagerImpl imageFileTypeManager = (ImageFileTypeManagerImpl) ImageFileTypeManager.getInstance();

    Map<FileType, String> registeredFileTypes = imageFileTypeManager.getRegisteredFileTypes();

    for (Map.Entry<FileType, String> entry : registeredFileTypes.entrySet()) {
      consumer.consume(entry.getKey(), entry.getValue());
    }
  }
}

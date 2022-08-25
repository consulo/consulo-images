package consulo.images;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.component.extension.ExtensionPointName;
import consulo.virtualFileSystem.fileType.FileTypeConsumer;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface ImageFileTypeProvider {
  ExtensionPointName<ImageFileTypeProvider> EP_NAME = ExtensionPointName.create("com.intellij.images.imageFileTypeProvider");

  void register(@Nonnull FileTypeConsumer fileTypeConsumer);
}

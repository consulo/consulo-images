package consulo.images;

import javax.annotation.Nonnull;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.fileTypes.FileTypeConsumer;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
public interface ImageFileTypeProvider
{
	ExtensionPointName<ImageFileTypeProvider> EP_NAME = ExtensionPointName.create("com.intellij.images.imageFileTypeProvider");

	void register(@Nonnull FileTypeConsumer fileTypeConsumer);
}

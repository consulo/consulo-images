package consulo.images;

import java.util.Map;

import javax.annotation.Nonnull;

import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.intellij.images.fileTypes.impl.ImageFileTypeManagerImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
public class ImageFileTypeFactory extends FileTypeFactory
{
	@Override
	public void createFileTypes(@Nonnull FileTypeConsumer consumer)
	{
		ImageFileTypeManagerImpl imageFileTypeManager = (ImageFileTypeManagerImpl) ImageFileTypeManager.getInstance();

		Map<FileType, String> registeredFileTypes = imageFileTypeManager.getRegisteredFileTypes();

		for(Map.Entry<FileType, String> entry : registeredFileTypes.entrySet())
		{
			consumer.consume(entry.getKey(), entry.getValue());
		}
	}
}

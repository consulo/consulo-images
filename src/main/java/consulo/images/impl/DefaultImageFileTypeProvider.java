package consulo.images.impl;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.util.text.StringUtil;
import consulo.images.ImageFileType;
import consulo.images.ImageFileTypeProvider;
import org.intellij.images.vfs.IfsUtil;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.util.HashSet;
import java.util.Set;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
public class DefaultImageFileTypeProvider implements ImageFileTypeProvider
{
	@Override
	public void register(@Nonnull FileTypeConsumer fileTypeConsumer)
	{
		final Set<String> processed = new HashSet<>();

		final String[] readerFormatNames = ImageIO.getReaderFormatNames();
		for(String format : readerFormatNames)
		{
			final String ext = format.toLowerCase();
			processed.add(ext);
		}

		processed.add(IfsUtil.ICO_FORMAT.toLowerCase());

		fileTypeConsumer.consume(ImageFileType.INSTANCE, StringUtil.join(processed, FileTypeConsumer.EXTENSION_DELIMITER));
	}
}

package consulo.images.svg;

import javax.annotation.Nonnull;

import org.intellij.images.fileTypes.impl.SvgFileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import consulo.images.ImageFileTypeProvider;

/**
 * @author VISTALL
 * @since 2018-08-07
 */
public class SVGImageFileTypeProvider implements ImageFileTypeProvider
{
	@Override
	public void register(@Nonnull FileTypeConsumer fileTypeConsumer)
	{
		fileTypeConsumer.consume(SvgFileType.INSTANCE);
	}
}

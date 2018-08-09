package consulo.images;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.intellij.images.ImagesBundle;
import com.intellij.openapi.fileTypes.FileType;
import consulo.ui.image.Image;
import icons.ImagesIcons;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
public class ImageFileType implements FileType
{
	public static final ImageFileType INSTANCE = new ImageFileType();

	@Nonnull
	@Override
	public String getId()
	{
		return "Images";
	}

	@Nonnull
	@Override
	public String getDescription()
	{
		return ImagesBundle.message("images.filetype.description");
	}

	@Nonnull
	@Override
	public String getDefaultExtension()
	{
		return "";
	}

	@Nullable
	@Override
	public Image getIcon()
	{
		return ImagesIcons.ImagesFileType;
	}
}

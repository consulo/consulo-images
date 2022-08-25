package consulo.images;

import consulo.images.localize.ImagesLocalize;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.fileType.FileType;
import org.intellij.images.ImagesIcons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
public class ImageFileType implements FileType {
  public static final ImageFileType INSTANCE = new ImageFileType();

  @Nonnull
  @Override
  public String getId() {
    return "Images";
  }

  @Nonnull
  @Override
  public LocalizeValue getDescription() {
    return ImagesLocalize.imagesFiletypeDescription();
  }

  @Nonnull
  @Override
  public String getDefaultExtension() {
    return "";
  }

  @Nullable
  @Override
  public Image getIcon() {
    return ImagesIcons.ImagesFileType;
  }
}

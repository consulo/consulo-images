package consulo.images;

import consulo.images.icon.ImagesIconGroup;
import consulo.images.localize.ImagesLocalize;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import org.intellij.images.util.ImageInfo;
import org.intellij.images.util.ImageInfoReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 2018-08-09
 */
public class BinaryImageFileType implements ImageFileType {
  public static final BinaryImageFileType INSTANCE = new BinaryImageFileType();

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
    return ImagesIconGroup.imagesfiletype();
  }

  @Override
  @Nullable
  public ImageInfo getImageInfo(@Nonnull byte[] content) {
    return ImageInfoReader.getInfo(content);
  }
}

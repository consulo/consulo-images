package consulo.images.svg.internal;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.virtualFileSystem.VirtualFile;
import org.intellij.images.util.ImageInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author VISTALL
 * @since 25-Aug-22
 */
@ServiceAPI(ComponentScope.APPLICATION)
public interface SVGFileProcessor {
  @Nullable
  ImageInfo getImageInfo(@Nonnull byte[] content);

  double getImageMaxZoomFactor(@Nonnull VirtualFile file, @Nonnull Object component);
}

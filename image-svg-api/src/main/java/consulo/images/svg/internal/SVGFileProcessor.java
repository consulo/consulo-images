package consulo.images.svg.internal;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.virtualFileSystem.VirtualFile;
import org.intellij.images.util.ImageInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

/**
 * @author VISTALL
 * @since 25-Aug-22
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface SVGFileProcessor {
  void convert(VirtualFile svgFile, File pngFile);

  @Nullable
  ImageInfo getImageInfo(String filePath, @Nonnull byte[] content);

  double getImageMaxZoomFactor(@Nonnull VirtualFile file, @Nonnull Object component);
}

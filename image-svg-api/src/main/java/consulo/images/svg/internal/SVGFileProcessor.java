package consulo.images.svg.internal;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.virtualFileSystem.VirtualFile;
import org.intellij.images.util.ImageInfo;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.File;

/**
 * @author VISTALL
 * @since 2022-08-25
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface SVGFileProcessor {
    void convert(VirtualFile svgFile, File pngFile);

    @Nullable
    ImageInfo getImageInfo(String filePath, @Nonnull byte[] content);

    double getImageMaxZoomFactor(@Nonnull VirtualFile file, @Nonnull Object component);
}

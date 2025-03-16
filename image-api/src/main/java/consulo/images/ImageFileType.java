package consulo.images;

import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import org.intellij.images.util.ImageInfo;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 2022-08-25
 */
public interface ImageFileType extends FileType {
    @Nullable
    ImageInfo getImageInfo(@Nonnull String filePath, @Nonnull byte[] content);

    default double getImageMaxZoomFactor(@Nonnull VirtualFile file, @Nonnull Object uiComponent) {
        return Double.MAX_VALUE;
    }
}

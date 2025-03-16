package consulo.images.desktop.awt.impl;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.VirtualFile;
import org.intellij.images.ImageDocument;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.IOException;

/**
 * @author VISTALL
 * @since 2022-08-25
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface ImageProcessor {
    boolean accept(@Nonnull VirtualFile file);

    /**
     * Return pair format + imageProvider
     */
    @Nullable
    Pair<String, ImageDocument.ScaledImageProvider> read(@Nonnull VirtualFile file) throws IOException;
}

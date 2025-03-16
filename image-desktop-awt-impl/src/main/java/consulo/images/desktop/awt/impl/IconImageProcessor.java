package consulo.images.desktop.awt.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.VirtualFile;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.bytesource.ByteSourceArray;
import org.apache.commons.imaging.formats.ico.IcoImageParser;
import org.intellij.images.ImageDocument;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 2022-08-25
 */
@ExtensionImpl
public class IconImageProcessor implements ImageProcessor {
    private static final IcoImageParser ICO_IMAGE_PARSER = new IcoImageParser();

    @Override
    public boolean accept(@Nonnull VirtualFile file) {
        return IfsUtil.ICO_FORMAT.equals(file.getExtension());
    }

    @Override
    @Nullable
    public Pair<String, ImageDocument.ScaledImageProvider> read(@Nonnull VirtualFile file) throws IOException {
        try {
            BufferedImage image = ICO_IMAGE_PARSER.getBufferedImage(new ByteSourceArray(file.contentsToByteArray()), null);

            return Pair.create(IfsUtil.ICO_FORMAT, (scale, ancestor) -> image);
        }
        catch (ImageReadException ignore) {
        }
        return null;
    }
}

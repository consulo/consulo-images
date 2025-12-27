package consulo.images.desktop.awt.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.Application;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import org.intellij.images.ImageDocument;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 2022-08-25
 */
@ExtensionImpl(order = "last")
public class ImageIOImageProcessor implements ImageProcessor {
    private final Application myApplication;

    @Inject
    public ImageIOImageProcessor(Application application) {
        myApplication = application;
    }

    @Override
    public boolean accept(@Nonnull VirtualFile file) {
        return true;
    }

    @Override
    @Nullable
    public Pair<String, ImageDocument.ScaledImageProvider> read(@Nonnull VirtualFile file) throws IOException {
        try (ImageInputStream imageInputStream = ImageIOProxy.createImageInputStream(file.getInputStream())) {
            ImageReader imageReader = ImageIOProxy.getImageReader(myApplication, imageInputStream);
            if (imageReader != null) {
                try {
                    String formatName = imageReader.getFormatName();
                    ImageReadParam param = imageReader.getDefaultReadParam();
                    imageReader.setInput(imageInputStream, true, true);
                    int minIndex = imageReader.getMinIndex();
                    BufferedImage image = imageReader.read(minIndex, param);
                    return Pair.create(formatName, (zoom, ancestor) -> image);
                }
                finally {
                    imageReader.dispose();
                }
            }
        }

        return null;
    }
}

package consulo.images.desktop.awt.impl;

import consulo.application.Application;
import consulo.component.extension.SPIClassLoaderExtension;
import jakarta.annotation.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * @author VISTALL
 * @since 27/12/2025
 */
public class ImageIOProxy {
    public static List<String> getReaderFormatNames() {
        // do not use List.of() since it's copy array
        List<String> formats = new ArrayList<>(Arrays.asList(ImageIO.getReaderFormatNames()));
        forEachReader(imageReaderSpi -> {
            formats.addAll(Arrays.asList(imageReaderSpi.getFormatNames()));
            return null;
        });
        return formats;
    }

    public static ImageInputStream createImageInputStream(Object input) throws IOException {
        return ImageIO.createImageInputStream(input);
    }

    @Nullable
    public static ImageReader getImageReader(ImageInputStream input) {
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(input);
        while (iterator.hasNext()) {
            return iterator.next();
        }

        return forEachReader(imageReaderSpi -> {
            try {
                // Perform mark/reset as a defensive measure
                // even though plug-ins are supposed to take
                // care of it.
                boolean canDecode = false;
                input.mark();
                try {
                    canDecode = imageReaderSpi.canDecodeInput(input);
                }
                finally {
                    input.reset();
                }

                if (canDecode) {
                    return imageReaderSpi.createReaderInstance();
                }
            }
            catch (IOException ignored) {
            }
            return null;
        });
    }

    private static <T> T forEachReader(Function<ImageReaderSpi, T> f) {
        ClassLoader joinedClassLoader = SPIClassLoaderExtension.createJoinedClassLoader(Application.get(), ImageIO.class);

        ServiceLoader<ImageReaderSpi> loader = ServiceLoader.load(ImageReaderSpi.class, joinedClassLoader);

        for (ImageReaderSpi readerSpi : loader) {
            T v = f.apply(readerSpi);
            if (v != null) {
                return v;
            }
        }

        
        return null;
    }
}

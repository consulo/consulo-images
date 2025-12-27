package consulo.images.desktop.awt.impl;

import consulo.application.Application;
import consulo.component.extension.SPIClassLoaderExtension;
import consulo.container.plugin.PluginManager;
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
    public static List<String> getReaderFormatNames(Application application) {
        // do not use List.of() since it's copy array
        List<String> formats = new ArrayList<>(Arrays.asList(ImageIO.getReaderFormatNames()));
        forEachReader(application, imageReaderSpi -> {
            formats.addAll(Arrays.asList(imageReaderSpi.getFormatNames()));
            return null;
        });
        return formats;
    }

    public static ImageInputStream createImageInputStream(Object input) throws IOException {
        return ImageIO.createImageInputStream(input);
    }

    @Nullable
    public static ImageReader getImageReader(Application application, ImageInputStream input) {
        Iterator<ImageReader> iterator = ImageIO.getImageReaders(input);
        while (iterator.hasNext()) {
            return iterator.next();
        }

        return forEachReader(application, imageReaderSpi -> {
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

    private static <T> T forEachReader(Application application, Function<ImageReaderSpi, T> f) {
        List<ClassLoader> classLoaders = application.getExtensionPoint(SPIClassLoaderExtension.class)
            .collectMapped(extension -> {
                if (extension.getTargetClass() == ImageIO.class) {
                    return PluginManager.getPlugin(extension.getClass()).getPluginClassLoader();
                }
                return null;
            });

        for (ClassLoader classLoader : classLoaders) {
            ServiceLoader<ImageReaderSpi> loader = ServiceLoader.load(ImageReaderSpi.class, classLoader);
            for (ImageReaderSpi readerSpi : loader) {
                T v = f.apply(readerSpi);
                if (v != null) {
                    return v;
                }
            }
        }

        return null;
    }
}

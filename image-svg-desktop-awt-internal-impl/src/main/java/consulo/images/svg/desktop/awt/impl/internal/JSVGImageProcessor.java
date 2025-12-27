package consulo.images.svg.desktop.awt.impl.internal;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.geometry.size.FloatSize;
import com.github.weisj.jsvg.parser.SVGLoader;
import consulo.annotation.component.ExtensionImpl;
import consulo.images.desktop.awt.impl.ImageProcessor;
import consulo.images.svg.SVGFileType;
import consulo.logging.Logger;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.util.GraphicsUtil;
import consulo.util.lang.Pair;
import consulo.util.lang.ref.SimpleReference;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.intellij.images.ImageDocument;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author VISTALL
 * @since 2025-03-18
 */
@ExtensionImpl(id = "jsvg")
public class JSVGImageProcessor implements ImageProcessor {
    private static final Logger LOG = Logger.getInstance(JSVGImageProcessor.class);

    @Override
    public boolean accept(@Nonnull VirtualFile file) {
        return file.getFileType() == SVGFileType.INSTANCE;
    }

    @Override
    @Nullable
    public Pair<String, ImageDocument.ScaledImageProvider> read(@Nonnull VirtualFile file) throws IOException {
        SimpleReference<URL> url = SimpleReference.create();
        try {
            url.set(new File(file.getPath()).toURI().toURL());
        }
        catch (MalformedURLException ex) {
            LOG.warn(ex.getMessage());
            return null;
        }

        ImageDocument.CachedScaledImageProvider provider = new ImageDocument.CachedScaledImageProvider() {
            JBUI.ScaleContext.Cache<BufferedImage> cache = new JBUI.ScaleContext.Cache<>((ctx) ->
            {
                try (InputStream stream = file.getInputStream()) {
                    return toImage(stream);
                }
                catch (Throwable t) {
                    LOG.warn(url.get() + " " + t.getMessage());
                    return null;
                }
            });

            @Override
            public void clearCache() {
                cache.clear();
            }

            @Override
            public BufferedImage apply(Double zoom, Component ancestor) {
                JBUI.ScaleContext ctx = JBUI.ScaleContext.create(ancestor);
                ctx.update(JBUI.ScaleType.OBJ_SCALE.of(zoom));
                return cache.getOrProvide(ctx);
            }
        };
        return Pair.create(SVGFileType.SVG_EXTENSION, provider);
    }

    public static BufferedImage toImage(InputStream stream) {
        SVGLoader loader = new SVGLoader();

        SVGDocument document = loader.load(stream);

        FloatSize shape = document.size();

        BufferedImage image = new BufferedImage((int) shape.width, (int) shape.height, BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = image.getGraphics();

        GraphicsUtil.setupAAPainting(graphics);

        document.render(null, (Graphics2D) graphics);

        graphics.dispose();

        return image;
    }
}
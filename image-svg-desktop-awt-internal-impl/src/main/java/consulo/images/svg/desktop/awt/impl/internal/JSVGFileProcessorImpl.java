package consulo.images.svg.desktop.awt.impl.internal;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.geometry.size.FloatSize;
import consulo.annotation.component.ExtensionImpl;
import consulo.images.svg.internal.SVGFileProcessor;
import consulo.logging.Logger;
import consulo.util.io.UnsyncByteArrayInputStream;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.intellij.images.util.ImageInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author VISTALL
 * @since 2025-03-18
 */
@ExtensionImpl(id = "jsvg")
public class JSVGFileProcessorImpl implements SVGFileProcessor {
    private static final Logger LOG = Logger.getInstance(JSVGFileProcessorImpl.class);

    @Override
    public void convert(VirtualFile svgFile, File pngFile) {
        try (InputStream stream = Files.newInputStream(pngFile.toPath())) {
            Image image = JSVGImageProcessor.toImage(stream);
            
            ImageIO.write((BufferedImage) image, "png", pngFile);
        }
        catch (IOException e) {
            LOG.warn("File: " + svgFile.getPath(), e);
        }
    }

    @Override
    @Nullable
    public ImageInfo getImageInfo(String filePath, @Nonnull byte[] content) {
        try {
            com.github.weisj.jsvg.parser.SVGLoader svgLoader = new com.github.weisj.jsvg.parser.SVGLoader();

            SVGDocument document = svgLoader.load(new UnsyncByteArrayInputStream(content));
            if (document == null) {
                return null;
            }
            FloatSize size = document.size();
            return new ImageInfo((int) size.getWidth(), (int) size.getHeight(), 0);
        }
        catch (Throwable t) {
            LOG.warn("File: " + filePath, t);
        }
        return null;
    }

    @Override
    public double getImageMaxZoomFactor(@Nonnull VirtualFile file, @Nonnull Object uiComponent) {
        return Double.MAX_VALUE;
    }
}
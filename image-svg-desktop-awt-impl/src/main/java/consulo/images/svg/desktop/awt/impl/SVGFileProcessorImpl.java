package consulo.images.svg.desktop.awt.impl;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.geometry.size.FloatSize;
import consulo.annotation.component.ExtensionImpl;
import consulo.images.svg.internal.SVGFileProcessor;
import consulo.logging.Logger;
import consulo.ui.ex.awt.JBUI;
import consulo.util.io.UnsyncByteArrayInputStream;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Singleton;
import org.intellij.images.util.ImageInfo;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author VISTALL
 * @since 25-Aug-22
 */
@Singleton
@ExtensionImpl
public class SVGFileProcessorImpl implements SVGFileProcessor {
    private static final Logger LOG = Logger.getInstance(SVGFileProcessorImpl.class);

    @Override
    public void convert(VirtualFile svgFile, File pngFile) {
        try {
            Image image = SVGLoader.load(new File(svgFile.getPath()).toURI().toURL(), 1f);
            ImageIO.write((BufferedImage)image, "png", pngFile);
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
            return new ImageInfo((int)size.getWidth(), (int)size.getHeight(), 0);
        }
        catch (Throwable t) {
            LOG.warn("File: " + filePath, t);
        }
        return null;
    }

    @Override
    public double getImageMaxZoomFactor(@Nonnull VirtualFile file, @Nonnull Object uiComponent) {
        try {
            URL url = new File(file.getPath()).toURI().toURL();
            return Math.max(
                1,
                SVGLoader.getMaxZoomFactor(
                    url,
                    new ByteArrayInputStream(file.contentsToByteArray()),
                    JBUI.ScaleContext.create((java.awt.Component)uiComponent)
                )
            );
        }
        catch (Throwable t) {
            LOG.warn("File: " + file.getPath(), t);
        }
        return Double.MAX_VALUE;
    }
}

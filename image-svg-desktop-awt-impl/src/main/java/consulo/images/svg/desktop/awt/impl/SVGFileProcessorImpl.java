package consulo.images.svg.desktop.awt.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.svg.internal.SVGFileProcessor;
import consulo.logging.Logger;
import consulo.ui.ex.awt.JBUI;
import consulo.util.io.UnsyncByteArrayInputStream;
import consulo.util.lang.Couple;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Singleton;
import org.intellij.images.util.ImageInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
      ImageIO.write((BufferedImage) image, "png", pngFile);
    } catch (IOException e1) {
      LOG.warn(e1);
    }
  }

  @Override
  @Nullable
  public ImageInfo getImageInfo(@Nonnull byte[] content) {
    try {
      Couple<Integer> info = SVGLoader.loadInfo(null, new UnsyncByteArrayInputStream(content), 1f);
      return new ImageInfo(info.getFirst(), info.getSecond(), 0);
    } catch (Throwable t) {
      Logger.getInstance(SVGFileProcessorImpl.class).warn(t);
    }
    return null;
  }

  @Override
  public double getImageMaxZoomFactor(@Nonnull VirtualFile file, @Nonnull Object uiComponent) {
    try {
      URL url = new File(file.getPath()).toURI().toURL();
      return Math.max(1, SVGLoader.getMaxZoomFactor(url, new ByteArrayInputStream(file.contentsToByteArray()), JBUI.ScaleContext.create((java.awt.Component) uiComponent)));
    } catch (Throwable t) {
      Logger.getInstance(SVGFileProcessorImpl.class).warn(t);
    }
    return Double.MAX_VALUE;
  }
}

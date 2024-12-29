package consulo.images.svg.desktop.awt.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.desktop.awt.impl.IfsUtil;
import consulo.images.desktop.awt.impl.ImageProcessor;
import consulo.images.svg.SVGFileType;
import consulo.logging.Logger;
import consulo.ui.ex.awt.JBUI;
import consulo.util.lang.Pair;
import consulo.util.lang.ref.Ref;
import consulo.virtualFileSystem.VirtualFile;
import org.intellij.images.ImageDocument;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author VISTALL
 * @since 25-Aug-22
 */
@ExtensionImpl
public class SVGImageProcessor implements ImageProcessor {
  private static final Logger LOG = Logger.getInstance(SVGImageProcessor.class);

  @Override
  public boolean accept(@Nonnull VirtualFile file) {
    return file.getFileType() == SVGFileType.INSTANCE;
  }                                                                                              

  @Override
  @Nullable
  public Pair<String, ImageDocument.ScaledImageProvider> read(@Nonnull VirtualFile file) throws IOException {
    final Ref<URL> url = Ref.create();
    try {
      url.set(new File(file.getPath()).toURI().toURL());
    } catch (MalformedURLException ex) {
      LOG.warn(ex.getMessage());
      return null;
    }

    byte[] content = file.contentsToByteArray();
    try {
      // ensure svg can be displayed
      SVGLoader.load(url.get(), new ByteArrayInputStream(content), 1.0f);
    } catch (Throwable t) {
      LOG.warn(url.get() + " " + t.getMessage(), t);
      return null;
    }

    ImageDocument.CachedScaledImageProvider provider = new ImageDocument.CachedScaledImageProvider() {
      JBUI.ScaleContext.Cache<BufferedImage> cache = new JBUI.ScaleContext.Cache<>((ctx) ->
      {
        try {
          return SVGLoader.loadHiDPI(url.get(), new ByteArrayInputStream(content), ctx);
        } catch (Throwable t) {
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
    return Pair.create(IfsUtil.SVG_FORMAT, provider);
  }
}

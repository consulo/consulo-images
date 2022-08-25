package consulo.images.svg.desktop.awt.impl;

import consulo.annotation.component.ServiceImpl;
import consulo.images.svg.internal.SVGConvertService;
import consulo.logging.Logger;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Singleton;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author VISTALL
 * @since 25-Aug-22
 */
@Singleton
@ServiceImpl
public class SVGConvertServiceImpl implements SVGConvertService {
  private static final Logger LOG = Logger.getInstance(SVGConvertServiceImpl.class);

  @Override
  public void convert(VirtualFile svgFile, File pngFile) {
    try {
      Image image = SVGLoader.load(new File(svgFile.getPath()).toURI().toURL(), 1f);
      ImageIO.write((BufferedImage) image, "png", pngFile);
    } catch (IOException e1) {
      LOG.warn(e1);
    }
  }
}

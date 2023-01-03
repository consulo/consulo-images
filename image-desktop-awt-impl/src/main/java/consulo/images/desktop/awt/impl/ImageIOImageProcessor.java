package consulo.images.desktop.awt.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.VirtualFile;
import org.intellij.images.ImageDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author VISTALL
 * @since 25-Aug-22
 */
@ExtensionImpl(order = "last")
public class ImageIOImageProcessor implements ImageProcessor {
  @Override
  public boolean accept(@Nonnull VirtualFile file) {
    return true;
  }

  @Override
  @Nullable
  public Pair<String, ImageDocument.ScaledImageProvider> read(@Nonnull VirtualFile file) throws IOException {
    byte[] content = file.contentsToByteArray();
    InputStream inputStream = new ByteArrayInputStream(content, 0, content.length);
    try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream)) {
      Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
      if (imageReaders.hasNext()) {
        ImageReader imageReader = imageReaders.next();
        try {
          String formatName = imageReader.getFormatName();
          ImageReadParam param = imageReader.getDefaultReadParam();
          imageReader.setInput(imageInputStream, true, true);
          int minIndex = imageReader.getMinIndex();
          BufferedImage image = imageReader.read(minIndex, param);
          return Pair.create(formatName, (zoom, ancestor) -> image);
        } finally {
          imageReader.dispose();
        }
      }
    }

    return null;
  }
}

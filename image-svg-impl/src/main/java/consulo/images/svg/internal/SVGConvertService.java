package consulo.images.svg.internal;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.virtualFileSystem.VirtualFile;

import java.io.File;

/**
 * @author VISTALL
 * @since 25-Aug-22
 */
@ServiceAPI(ComponentScope.APPLICATION)
public interface SVGConvertService {
  void convert(VirtualFile svgFile, File pngFile);
}

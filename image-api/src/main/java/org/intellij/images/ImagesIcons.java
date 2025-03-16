package org.intellij.images;

import consulo.images.icon.ImagesIconGroup;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.image.Image;

@Deprecated
public class ImagesIcons {
    public static final Image ImagesFileType = ImagesIconGroup.imagesfiletype();
    public static final Image ThumbnailBlank = Image.empty(Image.DEFAULT_ICON_SIZE);
    public static final Image ThumbnailDirectory = PlatformIconGroup.nodesFolder();
    public static final Image ThumbnailToolWindow = ImagesIconGroup.thumbnailtoolwindow();
    public static final Image ToggleTransparencyChessboard = ImagesIconGroup.toggletransparencychessboard();
}

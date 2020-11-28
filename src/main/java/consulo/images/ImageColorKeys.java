package consulo.images;

import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

/**
 * @author VISTALL
 * @since 11/28/2020
 */
public interface ImageColorKeys
{
	ColorKey BACKGROUND_COLOR_KEY = ColorKey.createColorKey("IMAGES_BACKGROUND", JBColor.background());
	ColorKey WHITE_CELL_COLOR_KEY = ColorKey.createColorKey("IMAGES_WHITE_CELL_COLOR", Gray.xFF);
	ColorKey GRID_LINE_COLOR_KEY = ColorKey.createColorKey("IMAGES_GRID_LINE_COLOR", JBColor.DARK_GRAY);
	ColorKey BLACK_CELL_COLOR_KEY = ColorKey.createColorKey("IMAGES_WHITE_CELL_COLOR", Gray.xC0);
}

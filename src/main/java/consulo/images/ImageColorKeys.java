package consulo.images;

import com.intellij.openapi.editor.colors.EditorColorKey;
import com.intellij.ui.JBColor;
import consulo.awt.TargetAWT;
import consulo.ui.color.RGBColor;
import consulo.ui.style.StandardColors;

/**
 * @author VISTALL
 * @since 11/28/2020
 */
public interface ImageColorKeys
{
	EditorColorKey BACKGROUND_COLOR_KEY = EditorColorKey.createColorKey("IMAGES_BACKGROUND", TargetAWT.from(JBColor.background()));
	EditorColorKey WHITE_CELL_COLOR_KEY = EditorColorKey.createColorKey("IMAGES_WHITE_CELL_COLOR", StandardColors.WHITE);
	EditorColorKey GRID_LINE_COLOR_KEY = EditorColorKey.createColorKey("IMAGES_GRID_LINE_COLOR", TargetAWT.from(JBColor.DARK_GRAY));
	EditorColorKey BLACK_CELL_COLOR_KEY = EditorColorKey.createColorKey("IMAGES_WHITE_CELL_COLOR", new RGBColor(0xC0, 0xC0, 0xC0));
}

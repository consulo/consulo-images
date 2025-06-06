package consulo.images.impl.colorScheme;

import consulo.annotation.component.ExtensionImpl;
import consulo.colorScheme.EditorColorSchemeExtender;
import consulo.colorScheme.EditorColorsScheme;
import consulo.images.ImageColorKeys;
import consulo.ui.color.RGBColor;
import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 06/06/2024
 */
@ExtensionImpl
public class LightAdditionalTextAttributesProvider implements EditorColorSchemeExtender {
    @Nonnull
    @Override
    public String getColorSchemeId() {
        return EditorColorsScheme.DEFAULT_SCHEME_NAME;
    }

    @Override
    public void extend(Builder builder) {
        builder.add(ImageColorKeys.GRID_LINE_COLOR_KEY, new RGBColor(0x00, 0x99, 0xE5));
        builder.add(ImageColorKeys.CHESSBOARD_BLACK_CELL_COLOR_KEY, new RGBColor(0xEB, 0xEB, 0xEB));
        builder.add(ImageColorKeys.CHESSBOARD_WHITE_CELL_COLOR_KEY, new RGBColor(0xFF, 0xFF, 0xFF));
    }
}
package consulo.images.impl.setting;

import consulo.annotation.component.ExtensionImpl;
import consulo.colorScheme.TextAttributesKey;
import consulo.colorScheme.setting.AttributesDescriptor;
import consulo.colorScheme.setting.ColorDescriptor;
import consulo.images.ImageColorKeys;
import consulo.images.localize.ImagesLocalize;
import consulo.language.editor.colorScheme.setting.ColorSettingsPage;
import consulo.language.editor.highlight.DefaultSyntaxHighlighter;
import consulo.language.editor.highlight.SyntaxHighlighter;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Map;

/**
 * @author VISTALL
 * @since 2020-11-28
 */
@ExtensionImpl
public class ImagesColorSettingsPage implements ColorSettingsPage {
    @Nonnull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new DefaultSyntaxHighlighter();
    }

    @Nonnull
    @Override
    public String getDemoText() {
        return " ";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Nonnull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return new AttributesDescriptor[0];
    }

    @Nonnull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return new ColorDescriptor[]{
            new ColorDescriptor(
                ImagesLocalize.gridLineColorDescriptor(),
                ImageColorKeys.GRID_LINE_COLOR_KEY,
                ColorDescriptor.Kind.BACKGROUND
            ),
            new ColorDescriptor(
                ImagesLocalize.whiteCellColorDescriptor(),
                ImageColorKeys.WHITE_CELL_COLOR_KEY,
                ColorDescriptor.Kind.BACKGROUND
            ),
            new ColorDescriptor(
                ImagesLocalize.blackCellColorDescriptor(),
                ImageColorKeys.BLACK_CELL_COLOR_KEY,
                ColorDescriptor.Kind.BACKGROUND
            )
        };
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return ImagesLocalize.settingsPageName().get();
    }
}

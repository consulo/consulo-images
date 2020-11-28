package consulo.images.preferences.impl;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import consulo.images.ImageColorKeys;
import org.intellij.images.ImagesBundle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author VISTALL
 * @since 11/28/2020
 */
public class ImagesColorSettingsPage implements ColorSettingsPage
{
	@Nonnull
	@Override
	public SyntaxHighlighter getHighlighter()
	{
		return new PlainSyntaxHighlighter();
	}

	@Nonnull
	@Override
	public String getDemoText()
	{
		return " ";
	}

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()
	{
		return null;
	}

	@Nonnull
	@Override
	public AttributesDescriptor[] getAttributeDescriptors()
	{
		return new AttributesDescriptor[0];
	}

	@Nonnull
	@Override
	public ColorDescriptor[] getColorDescriptors()
	{
		return new ColorDescriptor[] {
				//new ColorDescriptor(ImagesBundle.message("background.color.descriptor"), ImageColorKeys.BACKGROUND_COLOR_KEY, ColorDescriptor.Kind.BACKGROUND),
				new ColorDescriptor(ImagesBundle.message("grid.line.color.descriptor"), ImageColorKeys.GRID_LINE_COLOR_KEY, ColorDescriptor.Kind.BACKGROUND),
				new ColorDescriptor(ImagesBundle.message("white.cell.color.descriptor"), ImageColorKeys.WHITE_CELL_COLOR_KEY, ColorDescriptor.Kind.BACKGROUND),
				new ColorDescriptor(ImagesBundle.message("black.cell.color.descriptor"), ImageColorKeys.BLACK_CELL_COLOR_KEY, ColorDescriptor.Kind.BACKGROUND)
		};
	}

	@Nonnull
	@Override
	public String getDisplayName()
	{
		return "Image Viewer";
	}
}

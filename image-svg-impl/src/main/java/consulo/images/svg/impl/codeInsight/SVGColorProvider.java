package consulo.images.svg.impl.codeInsight;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.access.RequiredWriteAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.WriteAction;
import consulo.images.svg.SVGFileType;
import consulo.language.ast.IElementType;
import consulo.language.psi.ElementColorProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.ui.color.ColorValue;
import consulo.ui.util.ColorValueUtil;
import consulo.xml.psi.xml.XmlAttribute;
import consulo.xml.psi.xml.XmlTokenType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Set;

/**
 * @author UNV
 * @since 2025-03-21
 */
@ExtensionImpl
public class SVGColorProvider implements ElementColorProvider {
    private static final Set<String> SVG_COLOR_ATTRS =
        Set.of("fill", "flood-color", "lighting-color", "stop-color", "stroke");

    @Nullable
    @Override
    @RequiredReadAction
    public ColorValue getColorFrom(@Nonnull PsiElement element) {
        IElementType type = element.getNode().getElementType();
        if (type == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN
            && element.getParent().getParent() instanceof XmlAttribute attr
            && SVG_COLOR_ATTRS.contains(attr.getName())) {

            PsiFile file = element.getContainingFile();
            if (file != null && file.getFileType() instanceof SVGFileType) {
                return ColorValueUtil.fromHex(element.getText());
            }
        }
        return null;
    }

    @Override
    @RequiredWriteAction
    public void setColorTo(@Nonnull PsiElement element, @Nonnull ColorValue color) {
        IElementType type = element.getNode().getElementType();
        if (type == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN
            && element.getParent().getParent() instanceof XmlAttribute attr
            && SVG_COLOR_ATTRS.contains(attr.getName())) {
            WriteAction.run(() -> attr.setValue(ColorValueUtil.toHtmlColor(color)));
        }
    }
}

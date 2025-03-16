package consulo.images.svg;

import consulo.language.Language;
import consulo.xml.lang.xml.XMLLanguage;

/**
 * @author VISTALL
 * @since 2021-12-10
 */
public class SVGLanguage extends Language {
    public static final SVGLanguage INSTANCE = new SVGLanguage();

    private SVGLanguage() {
        super(XMLLanguage.INSTANCE, "SVG", "image/svg+xml");
    }
}

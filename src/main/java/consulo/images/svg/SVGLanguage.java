package consulo.images.svg;

import com.intellij.lang.Language;
import com.intellij.lang.xml.XMLLanguage;

/**
 * @author VISTALL
 * @since 10/12/2021
 */
public class SVGLanguage extends Language
{
	public static final SVGLanguage INSTANCE = new SVGLanguage();

	private SVGLanguage()
	{
		super(XMLLanguage.INSTANCE, "SVG", "image/svg+xml");
	}
}

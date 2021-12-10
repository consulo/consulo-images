package consulo.images.svg;

import com.intellij.lang.xml.XMLParserDefinition;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.tree.IFileElementType;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 10/12/2021
 */
public class SVGParserDefinition extends XMLParserDefinition
{
	private static final IFileElementType SVG_FILE = new IFileElementType(SVGLanguage.INSTANCE);

	@Nonnull
	@Override
	public PsiFile createFile(FileViewProvider viewProvider)
	{
		return new XmlFileImpl(viewProvider, SVG_FILE);
	}

	@Nonnull
	@Override
	public IFileElementType getFileNodeType()
	{
		return SVG_FILE;
	}
}

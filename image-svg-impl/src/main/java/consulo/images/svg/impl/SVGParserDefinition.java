package consulo.images.svg.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.svg.SVGLanguage;
import consulo.language.Language;
import consulo.language.ast.IFileElementType;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiFile;
import consulo.xml.lang.xml.XMLParserDefinition;
import consulo.xml.psi.impl.source.xml.XmlFileImpl;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 10/12/2021
 */
@ExtensionImpl
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

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return SVGLanguage.INSTANCE;
	}
}

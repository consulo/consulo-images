/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.images.fileTypes.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.ide.highlighter.XmlLikeFileType;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.UIBasedFileType;
import consulo.ui.image.Image;
import icons.ImagesIcons;

/**
 * @author Konstantin Bulenkov
 */
public final class SvgFileType extends XmlLikeFileType implements UIBasedFileType
{
	public static final SvgFileType INSTANCE = new SvgFileType();

	private SvgFileType()
	{
		super(XMLLanguage.INSTANCE);
	}

	@Nonnull
	@Override
	public String getId()
	{
		return "SVG";
	}

	@Nonnull
	@Override
	public String getDescription()
	{
		return "Scalable Vector Graphics";
	}

	@Nonnull
	@Override
	public String getDefaultExtension()
	{
		return "svg";
	}

	@Nullable
	@Override
	public Image getIcon()
	{
		return ImagesIcons.ImagesFileType;
	}
}

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

import consulo.images.localize.ImagesLocalize;
import consulo.images.svg.SVGLanguage;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.fileType.UIBasedFileType;
import consulo.xml.ide.highlighter.XmlLikeFileType;
import org.intellij.images.ImagesIcons;

import javax.annotation.Nonnull;

/**
 * @author Konstantin Bulenkov
 */
public final class SvgFileType extends XmlLikeFileType implements UIBasedFileType {
  public static final SvgFileType INSTANCE = new SvgFileType();

  private SvgFileType() {
    super(SVGLanguage.INSTANCE);
  }

  @Nonnull
  @Override
  public String getId() {
    return "SVG";
  }

  @Nonnull
  @Override
  public LocalizeValue getDescription() {
    return ImagesLocalize.svgFileTypeDescription();
  }

  @Nonnull
  @Override
  public String getDefaultExtension() {
    return "svg";
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return ImagesIcons.ImagesFileType;
  }
}

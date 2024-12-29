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
package consulo.images.svg;

import consulo.application.Application;
import consulo.images.ImageFileType;
import consulo.images.icon.ImagesIconGroup;
import consulo.images.localize.ImagesLocalize;
import consulo.images.svg.internal.SVGFileProcessor;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.UIBasedFileType;
import consulo.xml.ide.highlighter.XmlLikeFileType;
import org.intellij.images.util.ImageInfo;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Konstantin Bulenkov
 */
public final class SVGFileType extends XmlLikeFileType implements UIBasedFileType, ImageFileType {
  public static final SVGFileType INSTANCE = new SVGFileType();

  private SVGFileType() {
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
    return ImagesIconGroup.imagesfiletype();
  }

  @Override
  @Nullable
  public ImageInfo getImageInfo(@Nonnull String filePath, @Nonnull byte[] content) {
    for (SVGFileProcessor processor : Application.get().getExtensionPoint(SVGFileProcessor.class)) {
      return processor.getImageInfo(filePath, content);
    }
    return null;
  }

  @Override
  public double getImageMaxZoomFactor(@Nonnull VirtualFile file, @Nonnull Object uiComponent) {
    for (SVGFileProcessor processor : Application.get().getExtensionPoint(SVGFileProcessor.class)) {
      return processor.getImageMaxZoomFactor(file, uiComponent);
    }
    return Double.MAX_VALUE;
  }
}

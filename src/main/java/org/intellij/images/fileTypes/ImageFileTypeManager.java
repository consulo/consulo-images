/*
 * Copyright 2004-2005 Alexey Efimov
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
package org.intellij.images.fileTypes;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.ide.ServiceManager;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * File type manager.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
@ServiceAPI(ComponentScope.APPLICATION)
public abstract class ImageFileTypeManager {
  @Nonnull
  public static ImageFileTypeManager getInstance() {
    return ServiceManager.getService(ImageFileTypeManager.class);
  }

  /**
   * Check that file is image.
   *
   * @param file File to check
   * @return Return <code>true</code> if image file is file with Images file type
   */
  public abstract boolean isImage(@Nonnull VirtualFile file);

  @Nonnull
  public abstract FileType getImageFileType();

  public abstract Collection<FileType> getFileTypes();
}

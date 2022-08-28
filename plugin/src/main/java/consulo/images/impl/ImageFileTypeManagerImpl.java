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
package consulo.images.impl;

import consulo.annotation.component.ServiceImpl;
import consulo.images.BinaryImageFileType;
import consulo.images.ImageFileType;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.virtualFileSystem.fileType.FileTypeRegistry;
import jakarta.inject.Singleton;
import org.intellij.images.fileTypes.ImageFileTypeManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Image file type manager.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
@Singleton
@ServiceImpl
public final class ImageFileTypeManagerImpl extends ImageFileTypeManager {
  @Override
  public Collection<FileType> getFileTypes() {
    return Stream.of(FileTypeRegistry.getInstance().getRegisteredFileTypes()).filter(it -> it instanceof ImageFileType).toList();
  }

  @Override
  public boolean isImage(@Nonnull VirtualFile file) {
    return file.getFileType() instanceof ImageFileType;
  }

  @Nonnull
  @Override
  public FileType getBinaryImageFileType() {
    return BinaryImageFileType.INSTANCE;
  }
}

/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
package consulo.images.impl.index;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.ImageFileType;
import consulo.index.io.ID;
import consulo.index.io.data.DataExternalizer;
import consulo.index.io.data.DataInputOutputUtil;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.*;
import consulo.project.Project;
import consulo.util.collection.ContainerUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.intellij.images.util.ImageInfo;
import org.intellij.images.util.ImageInfoReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

/**
 * @author spleaner
 */
@ExtensionImpl
public class ImageInfoIndex extends SingleEntryFileBasedIndexExtension<ImageInfo> {
  private static final int ourMaxImageSize;

  static {
    int maxImageSize = 10;
    try {
      maxImageSize = Integer.parseInt(System.getProperty("idea.max.image.filesize", Integer.toString(maxImageSize)), 10);
    } catch (NumberFormatException ex) {
    }
    ourMaxImageSize = maxImageSize * 1024 * 1024;
  }

  private static int VERSION = 7;

  public static final ID<Integer, ImageInfo> INDEX_ID = ID.create("ImageFileInfoIndex");

  private final DataExternalizer<ImageInfo> myValueExternalizer = new DataExternalizer<ImageInfo>() {
    @Override
    public void save(final DataOutput out, final ImageInfo info) throws IOException {
      DataInputOutputUtil.writeINT(out, info.width());
      DataInputOutputUtil.writeINT(out, info.height());
      DataInputOutputUtil.writeINT(out, info.bpp());
    }

    @Override
    public ImageInfo read(final DataInput in) throws IOException {
      return new ImageInfo(DataInputOutputUtil.readINT(in), DataInputOutputUtil.readINT(in), DataInputOutputUtil.readINT(in));
    }
  };

  private final SingleEntryIndexer<ImageInfo> myDataIndexer = new SingleEntryIndexer<ImageInfo>(false) {
    @Override
    protected ImageInfo computeValue(@Nonnull FileContent inputData) {
      FileType fileType = inputData.getFileType();
      if (fileType instanceof ImageFileType imageFileType) {
        return imageFileType.getImageInfo(inputData.getFile().getPath(), inputData.getContent());
      }
      return null;
    }
  };

  @Override
  @Nonnull
  public ID<Integer, ImageInfo> getName() {
    return INDEX_ID;
  }

  @Override
  @Nonnull
  public SingleEntryIndexer<ImageInfo> getIndexer() {
    return myDataIndexer;
  }

  public static void processValues(VirtualFile virtualFile, FileBasedIndex.ValueProcessor<ImageInfo> processor, Project project) {
    FileBasedIndex.getInstance().processValues(INDEX_ID, Math.abs(FileBasedIndex.getFileId(virtualFile)), virtualFile, processor, GlobalSearchScope
        .fileScope(project, virtualFile));
  }

  @Nonnull
  @Override
  public DataExternalizer<ImageInfo> getValueExternalizer() {
    return myValueExternalizer;
  }

  @Nonnull
  @Override
  public FileBasedIndex.InputFilter getInputFilter() {
    Collection<FileType> fileTypes = ImageFileTypeManager.getInstance().getFileTypes();
    return new DefaultFileTypeSpecificInputFilter(ContainerUtil.toArray(fileTypes, FileType.ARRAY_FACTORY)) {
      @Override
      public boolean acceptInput(@Nullable Project project, @Nonnull VirtualFile file) {
        return file.isInLocalFileSystem() && file.getLength() < ourMaxImageSize;
      }
    };
  }

  @Override
  public int getVersion() {
    Collection<FileType> fileTypes = ImageFileTypeManager.getInstance().getFileTypes();
    return VERSION + fileTypes.size();
  }
}

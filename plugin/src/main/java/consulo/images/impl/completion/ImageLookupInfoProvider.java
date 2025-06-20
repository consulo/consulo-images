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
package consulo.images.impl.completion;

import consulo.annotation.component.ExtensionImpl;
import consulo.images.impl.index.ImageInfoIndex;
import consulo.language.editor.FileLookupInfoProvider;
import consulo.project.Project;
import consulo.util.lang.Couple;
import consulo.util.lang.Pair;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;
import org.intellij.images.fileTypes.ImageFileTypeManager;

/**
 * @author spleaner
 */
@ExtensionImpl
public class ImageLookupInfoProvider extends FileLookupInfoProvider {
    @Override
    public Pair<String, String> getLookupInfo(@Nonnull VirtualFile file, Project project) {
        String[] s = new String[]{null};
        ImageInfoIndex.processValues(
            file,
            (file1, value) -> {
                s[0] = String.format("%sx%s", value.width(), value.height());
                return true;
            },
            project
        );

        return s[0] == null ? null : Couple.of(file.getName(), s[0]);
    }

    @Nonnull
    @Override
    public FileType[] getFileTypes() {
        return ImageFileTypeManager.getInstance().getFileTypes().toArray(FileType.EMPTY_ARRAY);
    }
}

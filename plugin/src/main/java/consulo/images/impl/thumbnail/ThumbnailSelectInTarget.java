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
package consulo.images.impl.thumbnail;

import consulo.annotation.component.ExtensionImpl;
import consulo.project.ui.view.SelectInContext;
import consulo.project.ui.view.SelectInTarget;
import consulo.virtualFileSystem.VirtualFile;
import consulo.project.Project;
import jakarta.inject.Inject;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.intellij.images.thumbnail.ThumbnailManager;
import org.intellij.images.thumbnail.ThumbnailView;

@ExtensionImpl
final class ThumbnailSelectInTarget implements SelectInTarget {
    @Inject
    public ThumbnailSelectInTarget() {
    }

    @Override
    public boolean canSelect(SelectInContext context) {
        VirtualFile virtualFile = context.getVirtualFile();
        return ImageFileTypeManager.getInstance().isImage(virtualFile) && virtualFile.getParent() != null;
    }

    @Override
    public void selectIn(SelectInContext context, boolean requestFocus) {
        VirtualFile virtualFile = context.getVirtualFile();
        VirtualFile parent = virtualFile.getParent();
        if (parent != null) {
            Project project = context.getProject();
            ThumbnailView thumbnailView = ThumbnailManager.getManager(project).getThumbnailView();
            thumbnailView.setRoot(parent);
            thumbnailView.setVisible(true);
            thumbnailView.setSelected(virtualFile, true);
            thumbnailView.scrollToSelection();
        }
    }

    @Override
    public String toString() {
        return getToolWindowId();
    }

    @Override
    public String getToolWindowId() {
        return ThumbnailView.TOOLWINDOW_ID;
    }

    @Override
    public String getMinorViewId() {
        return null;
    }

    @Override
    public float getWeight() {
        return 10;
    }
}

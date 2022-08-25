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

/**
 * $Id$
 */

package consulo.images.desktop.awt.impl.thumbnail;

import consulo.disposer.Disposer;
import consulo.images.desktop.awt.impl.IfsUtil;
import consulo.project.Project;
import consulo.project.ui.wm.ToolWindowManager;
import consulo.ui.ex.toolWindow.ToolWindow;
import consulo.ui.ex.toolWindow.ToolWindowAnchor;
import consulo.virtualFileSystem.VirtualFile;
import org.intellij.images.ImagesIcons;
import org.intellij.images.editor.ImageZoomModel;
import org.intellij.images.editor.actionSystem.ImageEditorActions;
import org.intellij.images.thumbnail.ThumbnailView;

import javax.annotation.Nonnull;
import javax.swing.*;

/**
 * Thumbnail view.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
final class ThumbnailViewImpl implements ThumbnailView {

  private final Project project;
  private final ToolWindow toolWindow;

  private boolean recursive = false;
  private VirtualFile root = null;
  private final ThumbnailViewUI myThubmnailViewUi;

  public ThumbnailViewImpl(Project project) {
    this.project = project;

    ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
    myThubmnailViewUi = new ThumbnailViewUI(this);
    toolWindow = windowManager.registerToolWindow(TOOLWINDOW_ID, myThubmnailViewUi, ToolWindowAnchor.BOTTOM);
    toolWindow.setIcon(ImagesIcons.ThumbnailToolWindow);
    setVisible(false);
  }

  private ThumbnailViewUI getUI() {
    return myThubmnailViewUi;
  }

  @Override
  public void setRoot(@Nonnull VirtualFile root) {
    this.root = root;
    updateUI();
  }

  @Override
  public VirtualFile getRoot() {
    return root;
  }

  @Override
  public boolean isRecursive() {
    return recursive;
  }

  @Override
  public void setRecursive(boolean recursive) {
    this.recursive = recursive;
    updateUI();
  }

  @Override
  public void setSelected(@Nonnull VirtualFile file, boolean selected) {
    if (isVisible()) {
      getUI().setSelected(file, selected);
    }
  }

  @Override
  public boolean isSelected(@Nonnull VirtualFile file) {
    return isVisible() && getUI().isSelected(file);
  }

  @Override
  @Nonnull
  public VirtualFile[] getSelection() {
    if (isVisible()) {
      return getUI().getSelection();
    }
    return VirtualFile.EMPTY_ARRAY;
  }

  @Override
  public void scrollToSelection() {
    if (isVisible()) {
      if (!toolWindow.isActive()) {
        toolWindow.activate(new LazyScroller());
      } else {
        getUI().scrollToSelection();
      }
    }
  }

  @Override
  public boolean isVisible() {
    return toolWindow.isAvailable();
  }

  @Override
  public void activate() {
    if (isVisible() && !toolWindow.isActive()) {
      toolWindow.activate(null);
    }
  }

  @Override
  public void setVisible(boolean visible) {
    toolWindow.setAvailable(visible, null);
    if (visible) {
      setTitle();
      getUI().refresh();
    } else {
      getUI().dispose();
    }
  }

  private void updateUI() {
    if (isVisible()) {
      setTitle();
      getUI().refresh();
    }
  }

  private void setTitle() {
    toolWindow.setTitle(root != null ? IfsUtil.getReferencePath(project, root) : null);
  }

  @Override
  @Nonnull
  public Project getProject() {
    return project;
  }

  @Override
  public void setTransparencyChessboardVisible(boolean visible) {
    if (isVisible()) {
      getUI().setTransparencyChessboardVisible(visible);
    }
  }

  @Override
  public boolean isTransparencyChessboardVisible() {
    return isVisible() && getUI().isTransparencyChessboardVisible();
  }

  @Override
  public boolean isEnabledForActionPlace(String place) {
    // Enable if it not for Editor
    return isVisible() && !ImageEditorActions.ACTION_PLACE.equals(place);
  }

  @Override
  public void dispose() {
    // Dispose UI
    Disposer.dispose(getUI());
    // Unregister ToolWindow
    ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
    windowManager.unregisterToolWindow(TOOLWINDOW_ID);
  }

  @Override
  public ImageZoomModel getZoomModel() {
    return ImageZoomModel.STUB;
  }

  @Override
  public void setGridVisible(boolean visible) {
  }

  @Override
  public boolean isGridVisible() {
    return false;
  }

  private final class LazyScroller implements Runnable {
    @Override
    public void run() {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          getUI().scrollToSelection();
        }
      });
    }
  }
}

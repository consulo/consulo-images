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
package consulo.images.desktop.awt.impl.editor;

import consulo.application.Application;
import consulo.disposer.Disposer;
import consulo.fileEditor.FileEditorManager;
import consulo.images.desktop.awt.impl.IfsUtil;
import consulo.project.Project;
import consulo.virtualFileSystem.RefreshQueue;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.event.VirtualFileEvent;
import consulo.virtualFileSystem.event.VirtualFileListener;
import consulo.virtualFileSystem.event.VirtualFilePropertyEvent;
import jakarta.annotation.Nonnull;
import kava.beans.PropertyChangeEvent;
import kava.beans.PropertyChangeListener;
import org.intellij.images.ImageDocument;
import org.intellij.images.editor.ImageEditor;
import org.intellij.images.editor.ImageZoomModel;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.intellij.images.thumbnail.actionSystem.ThumbnailViewActions;

import javax.swing.*;

/**
 * Image viewer implementation.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
public final class ImageEditorImpl implements ImageEditor {
    private final Project project;
    private final VirtualFile file;
    private final ImageEditorUI editorUI;
    private boolean disposed;

    public ImageEditorImpl(@Nonnull Project project, @Nonnull VirtualFile file) {
        this.project = project;
        this.file = file;

        editorUI = new ImageEditorUI(project.getApplication(), this);
        
        Disposer.register(this, editorUI);

        project.getApplication().getMessageBus().connect(this).subscribe(VirtualFileListener.class, new VirtualFileListener() {
            @Override
            public void propertyChanged(@Nonnull VirtualFilePropertyEvent event) {
                ImageEditorImpl.this.propertyChanged(event);
            }

            @Override
            public void contentsChanged(@Nonnull VirtualFileEvent event) {
                ImageEditorImpl.this.contentsChanged(event);
            }
        });

        setValue(file);
    }

    @Override
    public void setValue(VirtualFile file) {
        try {
            editorUI.setImageProvider(IfsUtil.getImageProvider(file), IfsUtil.getFormat(file));
        }
        catch (Exception e) {
            //     Error loading image file
            editorUI.setImageProvider(null, null);
        }
    }

    @Override
    public boolean isValid() {
        ImageDocument document = editorUI.getImageComponent().getDocument();
        return document.getValue() != null;
    }

    @Override
    public ImageEditorUI getComponent() {
        return editorUI;
    }

    @Override
    public JComponent getContentComponent() {
        return editorUI.getImageComponent();
    }

    @Override
    @Nonnull
    public VirtualFile getFile() {
        return file;
    }

    @Override
    @Nonnull
    public Project getProject() {
        return project;
    }

    @Override
    public ImageDocument getDocument() {
        return editorUI.getImageComponent().getDocument();
    }

    @Override
    public void setTransparencyChessboardVisible(boolean visible) {
        editorUI.getImageComponent().setTransparencyChessboardVisible(visible);
        editorUI.repaint();
    }

    @Override
    public boolean isTransparencyChessboardVisible() {
        return editorUI.getImageComponent().isTransparencyChessboardVisible();
    }

    @Override
    public boolean isEnabledForActionPlace(String place) {
        // Disable for thumbnails action
        return !ThumbnailViewActions.ACTION_PLACE.equals(place);
    }

    @Override
    public void setGridVisible(boolean visible) {
        editorUI.getImageComponent().setGridVisible(visible);
        editorUI.repaint();
    }

    @Override
    public boolean isGridVisible() {
        return editorUI.getImageComponent().isGridVisible();
    }

    @Override
    public void addPropertyChangeListener(@Nonnull PropertyChangeListener listener) {
        this.getComponent().getImageComponent().addPropertyChangeListener(event -> {
            PropertyChangeEvent editorEvent =
                new PropertyChangeEvent(this, event.getPropertyName(), event.getOldValue(), event.getNewValue());
            listener.propertyChange(editorEvent);
        });
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public ImageZoomModel getZoomModel() {
        return editorUI.getZoomModel();
    }

    @Override
    public void dispose() {
        disposed = true;
    }

    void propertyChanged(@Nonnull VirtualFilePropertyEvent event) {
        if (file.equals(event.getFile())) {
            // Change document
            file.refresh(
                true,
                false,
                () -> {
                    if (ImageFileTypeManager.getInstance().isImage(file)) {
                        setValue(file);
                    }
                    else {
                        setValue(null);
                        // Close editor
                        FileEditorManager editorManager = FileEditorManager.getInstance(project);
                        editorManager.closeFile(file);
                    }
                }
            );
        }
    }

    void contentsChanged(@Nonnull VirtualFileEvent event) {
        if (file.equals(event.getFile())) {
            // Change document
            Runnable postRunnable = () -> setValue(file);
            RefreshQueue.getInstance().refresh(true, false, postRunnable, Application.get().getCurrentModalityState(), file);
        }
    }
}

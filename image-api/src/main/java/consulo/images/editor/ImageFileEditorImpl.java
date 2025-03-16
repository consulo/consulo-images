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
package consulo.images.editor;

import consulo.disposer.Disposer;
import consulo.fileEditor.FileEditorState;
import consulo.fileEditor.FileEditorStateLevel;
import consulo.project.Project;
import consulo.proxy.EventDispatcher;
import consulo.util.dataholder.UserDataHolderBase;
import consulo.virtualFileSystem.VirtualFile;
import kava.beans.PropertyChangeListener;
import org.intellij.images.editor.ImageEditor;
import org.intellij.images.editor.ImageFileEditor;
import org.intellij.images.editor.ImageZoomModel;
import org.intellij.images.options.*;

import jakarta.annotation.Nonnull;

import javax.swing.*;

/**
 * Image Editor.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
public final class ImageFileEditorImpl extends UserDataHolderBase implements ImageFileEditor {
    private static final String NAME = "ImageFileEditor";

    private final ImageEditor imageEditor;
    private final EventDispatcher<PropertyChangeListener> myDispatcher = EventDispatcher.create(PropertyChangeListener.class);

    public ImageFileEditorImpl(@Nonnull Project project, @Nonnull VirtualFile file) {
        imageEditor = project.getInstance(ImageEditorFactory.class).create(file);
        Disposer.register(this, imageEditor);

        // Set background and grid default options
        Options options = OptionsManager.getInstance().getOptions();
        EditorOptions editorOptions = options.getEditorOptions();
        GridOptions gridOptions = editorOptions.getGridOptions();
        TransparencyChessboardOptions transparencyChessboardOptions = editorOptions.getTransparencyChessboardOptions();
        imageEditor.setGridVisible(gridOptions.isShowDefault());
        imageEditor.setTransparencyChessboardVisible(transparencyChessboardOptions.isShowDefault());

        imageEditor.addPropertyChangeListener(myDispatcher.getMulticaster());
    }

    @Override
    @Nonnull
    public JComponent getComponent() {
        return imageEditor.getComponent();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return imageEditor.getContentComponent();
    }

    @Override
    @Nonnull
    public String getName() {
        return NAME;
    }

    @Override
    @Nonnull
    public FileEditorState getState(@Nonnull FileEditorStateLevel level) {
        ImageZoomModel zoomModel = imageEditor.getZoomModel();
        return new ImageFileEditorState(
            imageEditor.isTransparencyChessboardVisible(),
            imageEditor.isGridVisible(),
            zoomModel.getZoomFactor(),
            zoomModel.isZoomLevelChanged()
        );
    }

    @Override
    public void setState(@Nonnull FileEditorState state) {
        if (state instanceof ImageFileEditorState) {
            Options options = OptionsManager.getInstance().getOptions();
            ZoomOptions zoomOptions = options.getEditorOptions().getZoomOptions();

            ImageFileEditorState editorState = (ImageFileEditorState)state;
            ImageZoomModel zoomModel = imageEditor.getZoomModel();
            imageEditor.setTransparencyChessboardVisible(editorState.isBackgroundVisible());
            imageEditor.setGridVisible(editorState.isGridVisible());
            if (editorState.isZoomFactorChanged() || !zoomOptions.isSmartZooming()) {
                zoomModel.setZoomFactor(editorState.getZoomFactor());
            }
            zoomModel.setZoomLevelChanged(editorState.isZoomFactorChanged());
        }
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void selectNotify() {
    }

    @Override
    public void deselectNotify() {
    }

    @Override
    public void addPropertyChangeListener(@Nonnull PropertyChangeListener listener) {
        myDispatcher.addListener(listener);
    }

    @Override
    public void removePropertyChangeListener(@Nonnull PropertyChangeListener listener) {
        myDispatcher.removeListener(listener);
    }

    @Override
    public void dispose() {
    }

    @Override
    @Nonnull
    public ImageEditor getImageEditor() {
        return imageEditor;
    }
}

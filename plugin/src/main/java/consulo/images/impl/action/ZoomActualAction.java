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
package consulo.images.impl.action;

import consulo.annotation.component.ActionImpl;
import consulo.images.localize.ImagesLocalize;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DumbAwareAction;
import consulo.ui.image.Image;
import jakarta.annotation.Nullable;
import org.intellij.images.editor.ImageEditor;
import org.intellij.images.editor.ImageZoomModel;
import org.intellij.images.editor.actionSystem.ImageEditorActionUtil;
import org.intellij.images.ui.ImageComponentDecorator;

import jakarta.annotation.Nonnull;

/**
 * Resize image to actual size.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 * @see ImageEditor#getZoomModel()
 * @see ImageZoomModel#setZoomFactor
 */
@ActionImpl(id = "Images.Editor.Zoom.Actual")
// TODO <keyboard-shortcut first-keystroke="control DIVIDE" keymap="$default"/>
// TODO <keyboard-shortcut first-keystroke="control SLASH"keymap="$default"/>
public final class ZoomActualAction extends DumbAwareAction {
    public ZoomActualAction() {
        super(
            ImagesLocalize.actionImagesEditorZoomActualText(),
            LocalizeValue.empty(),
            PlatformIconGroup.graphActualzoom()
        );
    }

    @Override
    @RequiredUIAccess
    public void actionPerformed(@Nonnull AnActionEvent e) {
        ImageComponentDecorator decorator = ImageEditorActionUtil.getImageComponentDecorator(e);
        if (decorator != null) {
            decorator.getZoomModel().setZoomFactor(1.0d);
        }
    }

    @Override
    @RequiredUIAccess
    public void update(@Nonnull AnActionEvent e) {
        ImageComponentDecorator decorator = ImageEditorActionUtil.getImageComponentDecorator(e);
        e.getPresentation().setEnabled(decorator != null && decorator.getZoomModel().getZoomFactor() != 1.0d);
    }
}

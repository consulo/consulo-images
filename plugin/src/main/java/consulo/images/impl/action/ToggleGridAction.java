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
import consulo.images.icon.ImagesIconGroup;
import consulo.images.localize.ImagesLocalize;
import consulo.localize.LocalizeValue;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.Presentation;
import consulo.ui.ex.action.ToggleAction;
import jakarta.annotation.Nonnull;
import org.intellij.images.editor.ImageEditor;
import org.intellij.images.editor.actionSystem.ImageEditorActionUtil;
import org.intellij.images.ui.ImageComponentDecorator;

/**
 * Toggle grid lines over image.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 * @see ImageEditor#setGridVisible
 */
@ActionImpl(id = "Images.Editor.Toggle.Grid")
// TODO <keyboard-shortcut first-keystroke="control QUOTE" keymap="$default"/>
public final class ToggleGridAction extends ToggleAction {
    public ToggleGridAction() {
        super(ImagesLocalize.actionImagesEditorToggleGridShowText(), LocalizeValue.empty(), ImagesIconGroup.actionGrid());
    }

    @Override
    public boolean isSelected(@Nonnull AnActionEvent e) {
        return ImageEditorActionUtil.testImageDecorator(e, ImageComponentDecorator::isGridVisible);
    }

    @Override
    public void setSelected(@Nonnull AnActionEvent e, boolean state) {
        ImageEditorActionUtil.acceptImageDecorator(e, decorator -> decorator.setGridVisible(state));
    }

    @Override
    @RequiredUIAccess
    public void update(@Nonnull AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(ImageEditorActionUtil.testImageDecorator(e, decorator -> true));
        presentation.setTextValue(
            isSelected(e) ? ImagesLocalize.actionImagesEditorToggleGridHideText() : ImagesLocalize.actionImagesEditorToggleGridShowText()
        );
    }
}

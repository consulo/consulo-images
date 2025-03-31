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
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.Presentation;
import consulo.ui.ex.action.ToggleAction;
import jakarta.annotation.Nonnull;
import org.intellij.images.editor.actionSystem.ImageEditorActionUtil;
import org.intellij.images.ui.ImageComponentDecorator;

/**
 * Show/hide background action.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 * @see ImageComponentDecorator#setTransparencyChessboardVisible
 */
@ActionImpl(id = "Images.ToggleTransparencyChessboard")
public final class ToggleTransparencyChessboardAction extends ToggleAction {
    public ToggleTransparencyChessboardAction() {
        super(
            ImagesLocalize.actionImagesEditorToggleTransparencyChessboardShowText(),
            ImagesLocalize.actionImagesEditorToggleTransparencyChessboardDescription(),
            ImagesIconGroup.actionChessboard()
        );
    }

    @Override
    public boolean isSelected(@Nonnull AnActionEvent e) {
        return ImageEditorActionUtil.testImageDecorator(e, ImageComponentDecorator::isTransparencyChessboardVisible);
    }

    @Override
    public void setSelected(@Nonnull AnActionEvent e, boolean state) {
        ImageEditorActionUtil.acceptImageDecorator(e, decorator -> decorator.setTransparencyChessboardVisible(state));
    }

    @Override
    @RequiredUIAccess
    public void update(@Nonnull AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        presentation.setEnabled(ImageEditorActionUtil.testImageDecorator(e, decorator -> true));
        presentation.setTextValue(
            isSelected(e)
                ? ImagesLocalize.actionImagesEditorToggleTransparencyChessboardHideText()
                : ImagesLocalize.actionImagesEditorToggleTransparencyChessboardShowText()
        );
    }
}

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
package org.intellij.images.editor.actions;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionRef;
import consulo.images.localize.ImagesLocalize;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.IdeActions;
import org.intellij.images.editor.ImageEditor;
import org.intellij.images.editor.ImageZoomModel;
import org.intellij.images.editor.actionSystem.ImageEditorActionUtil;
import org.intellij.images.ui.ImageComponentDecorator;

import javax.annotation.Nonnull;

/**
 * Zoom in.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 * @see ImageEditor#getZoomModel
 */
@ActionImpl(id = "Images.Editor.ZoomIn", shortcutFrom = @ActionRef(id = IdeActions.ACTION_EXPAND_ALL))
public final class ZoomInAction extends AnAction {
  public ZoomInAction() {
    super(ImagesLocalize.actionImagesEditorZoominText(), LocalizeValue.of(), PlatformIconGroup.graphZoomin());
  }

  @Override
  @RequiredUIAccess
  public void actionPerformed(@Nonnull AnActionEvent e) {
    ImageComponentDecorator decorator = ImageEditorActionUtil.getImageComponentDecorator(e);
    if (decorator != null) {
      ImageZoomModel zoomModel = decorator.getZoomModel();
      zoomModel.zoomIn();
    }
  }

  @Override
  @RequiredUIAccess
  public void update(AnActionEvent e) {
    ImageComponentDecorator decorator = ImageEditorActionUtil.getImageComponentDecorator(e);
    e.getPresentation().setEnabled(decorator != null && decorator.getZoomModel().canZoomIn());
  }
}

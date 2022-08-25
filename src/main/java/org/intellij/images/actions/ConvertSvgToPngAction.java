/*
 * Copyright 2000-2017 JetBrains s.r.o.
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
package org.intellij.images.actions;

import com.intellij.util.SVGLoader;
import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.language.editor.CommonDataKeys;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DumbAwareAction;
import consulo.virtualFileSystem.VirtualFile;
import org.intellij.images.fileTypes.impl.SvgFileType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Konstantin Bulenkov
 */
@ActionImpl(id = "Images.ConvertSvgToPng", parents = {
    @ActionParentRef(value = @ActionRef(id = "ProjectViewPopupMenu"), anchor = ActionRefAnchor.AFTER, relatedToAction = @ActionRef(id = "EditSource"))
})
public class ConvertSvgToPngAction extends DumbAwareAction {
  public ConvertSvgToPngAction() {
    super("Convert to PNG");
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    VirtualFile svgFile = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
    try {
      Image image = SVGLoader.load(new File(svgFile.getPath()).toURI().toURL(), 1f);
      String path = svgFile.getPath();
      ImageIO.write((BufferedImage) image, "png", new File(path + ".png"));
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  @Override
  public void update(AnActionEvent e) {
    VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
    boolean enabled = file != null && file.getFileType() == SvgFileType.INSTANCE;
    e.getPresentation().setEnabledAndVisible(enabled);
  }
}

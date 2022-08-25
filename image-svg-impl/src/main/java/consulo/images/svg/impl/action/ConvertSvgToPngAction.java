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
package consulo.images.svg.impl.action;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.images.svg.internal.SVGConvertService;
import consulo.images.svg.SVGFileType;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DumbAwareAction;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.io.File;

/**
 * @author Konstantin Bulenkov
 */
@ActionImpl(id = "Images.ConvertSvgToPng", parents = {
    @ActionParentRef(value = @ActionRef(id = "ProjectViewPopupMenu"), anchor = ActionRefAnchor.AFTER, relatedToAction = @ActionRef(id = "EditSource"))
})
public class ConvertSvgToPngAction extends DumbAwareAction {
  private final Provider<SVGConvertService> mySvgConvertServiceProvider;

  @Inject
  public ConvertSvgToPngAction(Provider<SVGConvertService> svgConvertServiceProvider) {
    super("Convert to PNG");
    mySvgConvertServiceProvider = svgConvertServiceProvider;
  }

  @RequiredUIAccess
  @Override
  public void actionPerformed(AnActionEvent e) {
    VirtualFile svgFile = e.getRequiredData(VirtualFile.KEY);

    String path = svgFile.getPath();

    mySvgConvertServiceProvider.get().convert(svgFile, new File(path + ".png"));
  }

  @RequiredUIAccess
  @Override
  public void update(AnActionEvent e) {
    VirtualFile svgFile = e.getData(VirtualFile.KEY);
    boolean enabled = svgFile != null && svgFile.getFileType() == SVGFileType.INSTANCE;
    e.getPresentation().setEnabledAndVisible(enabled);
  }
}

/*
 * Copyright 2000-2013 JetBrains s.r.o.
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

import consulo.images.impl.setting.ImagesOptionsConfigurable;
import consulo.images.localize.ImagesLocalize;
import consulo.platform.Platform;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.local.EnvironmentUtil;
import consulo.process.local.ExecUtil;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.ActionPlaces;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.awt.Messages;
import consulo.util.io.FileUtil;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import jakarta.annotation.Nonnull;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.intellij.images.options.Options;
import org.intellij.images.options.OptionsManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Open image file externally.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
public final class EditExternallyAction extends AnAction {
    @RequiredUIAccess
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(Project.KEY);
        VirtualFile[] files = e.getData(VirtualFile.KEY_OF_ARRAY);
        if (files == null) {
            return;
        }
        Options options = OptionsManager.getInstance().getOptions();
        String executablePath = options.getExternalEditorOptions().getExecutablePath();
        if (StringUtil.isEmpty(executablePath)) {
            for (VirtualFile file : files) {
                try {
                    // TODO replace it by impl from Platform
                    Desktop.getDesktop().open(VirtualFileUtil.virtualToIoFile(file));
                }
                catch (IOException e1) {
                    Messages.showErrorDialog(
                        project,
                        ImagesLocalize.errorEmptyExternalEditorPath().get(),
                        ImagesLocalize.errorTitleEmptyExternalEditorPath().get()
                    );

                    ImagesOptionsConfigurable.show(project);
                    break;
                }
            }
        }
        else {
            Map<String, String> env = EnvironmentUtil.getEnvironmentMap();
            for (String varName : env.keySet()) {
                if (Platform.current().os().isWindows()) {
                    executablePath = StringUtil.replace(executablePath, "%" + varName + "%", env.get(varName), true);
                }
                else {
                    executablePath = StringUtil.replace(executablePath, "${" + varName + "}", env.get(varName), false);
                }
            }
            executablePath = FileUtil.toSystemDependentName(executablePath);
            File executable = new File(executablePath);
            GeneralCommandLine commandLine = new GeneralCommandLine();
            String path = executable.exists() ? executable.getAbsolutePath() : executablePath;
            if (Platform.current().os().isMac()) {
                commandLine.setExePath(ExecUtil.getOpenCommandPath());
                commandLine.addParameter("-a");
                commandLine.addParameter(path);
            }
            else {
                commandLine.setExePath(path);
            }

            ImageFileTypeManager typeManager = ImageFileTypeManager.getInstance();
            for (VirtualFile file : files) {
                if (file.isInLocalFileSystem() && typeManager.isImage(file)) {
                    commandLine.addParameter(VirtualFileUtil.virtualToIoFile(file).getAbsolutePath());
                }
            }
            commandLine.setWorkDirectory(new File(executablePath).getParentFile());

            try {
                commandLine.createProcess();
            }
            catch (ExecutionException ex) {
                Messages.showErrorDialog(project, ex.getLocalizedMessage(), ImagesLocalize.errorTitleLaunchingExternalEditor().get());
                ImagesOptionsConfigurable.show(project);
            }
        }
    }

    @Override
    @RequiredUIAccess
    public void update(@Nonnull AnActionEvent e) {
        doUpdate(e);
    }

    static void doUpdate(AnActionEvent e) {
        VirtualFile[] files = e.getData(VirtualFile.KEY_OF_ARRAY);
        boolean isEnabled = isImages(files);
        if (e.getPlace().equals(ActionPlaces.PROJECT_VIEW_POPUP)) {
            e.getPresentation().setVisible(isEnabled);
        }
        else {
            e.getPresentation().setEnabled(isEnabled);
        }
    }

    private static boolean isImages(VirtualFile[] files) {
        boolean isImagesFound = false;
        if (files != null) {
            ImageFileTypeManager typeManager = ImageFileTypeManager.getInstance();
            for (VirtualFile file : files) {
                boolean isImage = typeManager.isImage(file);
                isImagesFound |= isImage;
                if (!file.isInLocalFileSystem() || !isImage) {
                    return false;
                }
            }
        }
        return isImagesFound;
    }
}

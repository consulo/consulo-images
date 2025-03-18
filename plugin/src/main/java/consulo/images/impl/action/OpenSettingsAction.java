package consulo.images.impl.action;

import consulo.annotation.component.ActionImpl;
import consulo.ide.setting.ShowSettingsUtil;
import consulo.images.impl.setting.ImagesOptionsConfigurable;
import consulo.images.localize.ImagesLocalize;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DumbAwareAction;
import consulo.ui.ex.awt.LocalizeAction;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Open Image Viewer settings.
 *
 * @author UNV
 * @since 2024-06-06
 */
@ActionImpl(id = "Images.Open.Settings")
public class OpenSettingsAction extends DumbAwareAction {
    public OpenSettingsAction() {
        super(
            ImagesLocalize.actionImagesOpenSettingsText(),
            ImagesLocalize.actionImagesOpenSettingsDescription(),
            PlatformIconGroup.generalSettings()
        );
    }

    @Override
    @RequiredUIAccess
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getDataContext().getData(Project.KEY);
        ShowSettingsUtil.getInstance().showSettingsDialog(project, ImagesOptionsConfigurable.class);
    }
}
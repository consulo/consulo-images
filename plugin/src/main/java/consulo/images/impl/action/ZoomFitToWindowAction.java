package consulo.images.impl.action;

import consulo.annotation.component.ActionImpl;
import consulo.images.icon.ImagesIconGroup;
import consulo.images.localize.ImagesLocalize;
import consulo.localize.LocalizeValue;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DumbAwareAction;
import jakarta.annotation.Nonnull;
import org.intellij.images.editor.ImageZoomModel;
import org.intellij.images.editor.actionSystem.ImageEditorActionUtil;

/**
 * @author UNV
 * @since 2025-03-31
 */
@ActionImpl(id = "Images.Editor.Zoom.Fit.To.Window")
public class ZoomFitToWindowAction extends DumbAwareAction {
    public ZoomFitToWindowAction() {
        super(
            ImagesLocalize.actionImagesEditorZoomFitToWindowText(),
            LocalizeValue.empty(),
            ImagesIconGroup.actionFitcontent()
        );
    }

    @Override
    @RequiredUIAccess
    public void actionPerformed(@Nonnull AnActionEvent e) {
        ImageEditorActionUtil.acceptZoomModel(e, ImageZoomModel::zoomFitToWindow);
    }

    @Override
    @RequiredUIAccess
    public void update(@Nonnull AnActionEvent e) {
        e.getPresentation().setEnabled(ImageEditorActionUtil.testZoomModel(e, ImageZoomModel::canZoomFitToWindow));
    }
}

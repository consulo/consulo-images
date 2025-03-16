package consulo.images.impl.action;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionRef;
import consulo.ui.ex.action.AnSeparator;
import consulo.ui.ex.action.DefaultActionGroup;
import org.intellij.images.editor.actionSystem.ImageEditorActions;
import org.intellij.images.editor.actions.ActualSizeAction;
import org.intellij.images.editor.actions.ToggleGridAction;
import org.intellij.images.editor.actions.ZoomInAction;
import org.intellij.images.editor.actions.ZoomOutAction;

/**
 * @author VISTALL
 * @since 06/06/2024
 */
@ActionImpl(id = ImageEditorActions.GROUP_TOOLBAR, children = {
    @ActionRef(type = ToggleTransparencyChessboardAction.class),
    @ActionRef(type = ToggleGridAction.class),
    @ActionRef(type = AnSeparator.class),
    @ActionRef(type = ZoomInAction.class),
    @ActionRef(type = ZoomOutAction.class),
    @ActionRef(type = ActualSizeAction.class)
})
public class ImagesEditorToolbarGroup extends DefaultActionGroup {
}

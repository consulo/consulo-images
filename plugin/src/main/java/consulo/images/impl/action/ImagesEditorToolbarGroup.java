package consulo.images.impl.action;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionRef;
import consulo.ui.ex.action.AnSeparator;
import consulo.ui.ex.action.DefaultActionGroup;
import org.intellij.images.editor.actionSystem.ImageEditorActions;

/**
 * @author VISTALL
 * @since 2024-06-06
 */
@ActionImpl(id = ImageEditorActions.GROUP_TOOLBAR, children = {
    @ActionRef(type = ToggleTransparencyChessboardAction.class),
    @ActionRef(type = ToggleGridAction.class),
    @ActionRef(type = AnSeparator.class),
    @ActionRef(type = ZoomInAction.class),
    @ActionRef(type = ZoomOutAction.class),
    @ActionRef(type = ZoomActualAction.class),
    @ActionRef(type = AnSeparator.class),
    @ActionRef(type = OpenSettingsAction.class)
})
public class ImagesEditorToolbarGroup extends DefaultActionGroup {
}

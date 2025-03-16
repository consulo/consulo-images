package consulo.images.editor;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.virtualFileSystem.VirtualFile;
import org.intellij.images.editor.ImageEditor;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 25-Aug-22
 */
@ServiceAPI(ComponentScope.PROJECT)
public interface ImageEditorFactory {
    @Nonnull
    ImageEditor create(@Nonnull VirtualFile imageFile);
}

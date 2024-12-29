package consulo.images.desktop.awt.impl.editor;

import consulo.annotation.component.ServiceImpl;
import consulo.images.editor.ImageEditorFactory;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.intellij.images.editor.ImageEditor;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 25-Aug-22
 */
@Singleton
@ServiceImpl
public class ImageEditorFactoryImpl implements ImageEditorFactory {
  private final Project myProject;

  @Inject
  public ImageEditorFactoryImpl(Project project) {
    myProject = project;
  }

  @Override
  @Nonnull
  public ImageEditor create(@Nonnull VirtualFile imageFile) {
    return new ImageEditorImpl(myProject, imageFile);
  }
}

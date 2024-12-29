package consulo.images.svg.impl.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.concurrent.ApplicationConcurrency;
import consulo.application.dumb.DumbAware;
import consulo.document.event.DocumentEvent;
import consulo.document.event.DocumentListener;
import consulo.fileEditor.*;
import consulo.fileEditor.text.TextEditorProvider;
import consulo.images.editor.ImageFileEditorImpl;
import consulo.images.svg.SVGFileType;
import consulo.images.svg.internal.SVGFileProcessor;
import consulo.language.file.light.LightVirtualFile;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Inject;
import org.intellij.images.editor.ImageFileEditor;

import jakarta.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author VISTALL
 * @since 2018-08-08
 */
@ExtensionImpl
public class SVGFileEditorProvider implements FileEditorProvider, DumbAware {
  private final TextEditorWithPreviewFactory myTextEditorWithPreviewFactory;
  private final ApplicationConcurrency myApplicationConcurrency;

  @Inject
  public SVGFileEditorProvider(TextEditorWithPreviewFactory textEditorWithPreviewFactory, ApplicationConcurrency applicationConcurrency) {
    myTextEditorWithPreviewFactory = textEditorWithPreviewFactory;
    myApplicationConcurrency = applicationConcurrency;
  }

  @Override
  public boolean accept(@Nonnull Project project, @Nonnull VirtualFile virtualFile) {
    return virtualFile.getFileType() == SVGFileType.INSTANCE && project.getApplication().getExtensionPoint(SVGFileProcessor.class).hasAnyExtensions();
  }

  @RequiredUIAccess
  @Nonnull
  @Override
  public FileEditor createEditor(@Nonnull Project project, @Nonnull VirtualFile file) {
    ImageFileEditor viewer = new ImageFileEditorImpl(project, file);

    TextEditor editor = (TextEditor) TextEditorProvider.getInstance().createEditor(project, file);
    editor.getEditor().getDocument().addDocumentListener(new DocumentListener() {
      private Future<?> myFuture = CompletableFuture.completedFuture(null);

      @Override
      public void documentChanged(DocumentEvent event) {
        myFuture.cancel(false);
        myFuture = myApplicationConcurrency.getScheduledExecutorService().schedule(() -> {
          viewer.getImageEditor().setValue(new LightVirtualFile("preview.svg", file.getFileType(), event.getDocument().getText()));
        }, 500, TimeUnit.MILLISECONDS);
      }
    }, editor);
    return myTextEditorWithPreviewFactory.create(editor, viewer, "SvgEditor");
  }

  @Nonnull
  @Override
  public String getEditorTypeId() {
    return "svg.images";
  }

  @Nonnull
  @Override
  public FileEditorPolicy getPolicy() {
    return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
  }
}

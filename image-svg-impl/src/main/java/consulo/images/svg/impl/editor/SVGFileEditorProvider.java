package consulo.images.svg.impl.editor;

import consulo.annotation.component.ExtensionImpl;
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
import consulo.ui.ex.awt.util.Alarm;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Inject;
import org.intellij.images.editor.ImageFileEditor;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 2018-08-08
 */
@ExtensionImpl
public class SVGFileEditorProvider implements FileEditorProvider, DumbAware {
  private final TextEditorWithPreviewFactory myTextEditorWithPreviewFactory;

  @Inject
  public SVGFileEditorProvider(TextEditorWithPreviewFactory textEditorWithPreviewFactory) {
    myTextEditorWithPreviewFactory = textEditorWithPreviewFactory;
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
      Alarm myAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, editor);

      @Override
      public void documentChanged(DocumentEvent event) {
        myAlarm.cancelAllRequests();
        myAlarm.addRequest(() -> viewer.getImageEditor().setValue(new LightVirtualFile("preview.svg", file.getFileType(), event.getDocument().getText())),
            500);
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

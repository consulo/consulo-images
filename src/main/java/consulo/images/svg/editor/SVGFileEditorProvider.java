package consulo.images.svg.editor;

import javax.annotation.Nonnull;

import org.intellij.images.editor.impl.ImageEditorImpl;
import org.intellij.images.editor.impl.ImageFileEditorImpl;
import org.intellij.images.fileTypes.impl.SvgFileType;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.TextEditorWithPreview;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.Alarm;
import consulo.fileEditor.impl.text.TextEditorProvider;

/**
 * @author VISTALL
 * @since 2018-08-08
 */
public class SVGFileEditorProvider implements FileEditorProvider, DumbAware
{
	@Override
	public boolean accept(@Nonnull Project project, @Nonnull VirtualFile virtualFile)
	{
		return virtualFile.getFileType() == SvgFileType.INSTANCE;
	}

	@Nonnull
	@Override
	public FileEditor createEditor(@Nonnull Project project, @Nonnull VirtualFile file)
	{
		ImageFileEditorImpl viewer = new ImageFileEditorImpl(project, file);

		TextEditor editor = (TextEditor) TextEditorProvider.getInstance().createEditor(project, file);
		editor.getEditor().getDocument().addDocumentListener(new DocumentListener()
		{
			Alarm myAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, editor);

			@Override
			public void documentChanged(DocumentEvent event)
			{
				myAlarm.cancelAllRequests();
				myAlarm.addRequest(() -> ((ImageEditorImpl) viewer.getImageEditor()).setValue(new LightVirtualFile("preview.svg", file.getFileType(), event.getDocument().getText())),
						500);
			}
		}, editor);
		return new TextEditorWithPreview(editor, viewer, "SvgEditor");
	}

	@Nonnull
	@Override
	public String getEditorTypeId()
	{
		return "svg.images";
	}

	@Nonnull
	@Override
	public FileEditorPolicy getPolicy()
	{
		return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
	}
}

package consulo.images.preferences.impl;

import com.intellij.images.localize.ImagesLocalize;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import consulo.disposer.Disposable;
import consulo.disposer.Disposer;
import consulo.ide.ui.FileChooserTextBoxBuilder;
import consulo.options.SimpleConfigurableByProperties;
import consulo.platform.Platform;
import consulo.ui.CheckBox;
import consulo.ui.Component;
import consulo.ui.IntBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.border.BorderPosition;
import consulo.ui.border.BorderStyle;
import consulo.ui.layout.LabeledLayout;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.util.LabeledBuilder;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.intellij.images.ImagesBundle;
import org.intellij.images.options.*;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 11/28/2020
 */
public class ImagesOptionsConfigurabe extends SimpleConfigurableByProperties implements SearchableConfigurable
{
	@RequiredUIAccess
	public static void show(Project project)
	{
		final ShowSettingsUtil util = ShowSettingsUtil.getInstance();
		util.editConfigurable(project, new ImagesOptionsConfigurabe(OptionsManager::getInstance));
	}

	private Provider<OptionsManager> myOptionsManager;

	private Disposable myDisposable;

	@Inject
	public ImagesOptionsConfigurabe(Provider<OptionsManager> optionsManager)
	{
		myOptionsManager = optionsManager;
	}

	@RequiredUIAccess
	@Nonnull
	@Override
	protected Component createLayout(PropertyBuilder propertyBuilder)
	{
		myDisposable = Disposable.newDisposable();

		Options options = myOptionsManager.get().getOptions();
		GridOptions gridOptions = options.getEditorOptions().getGridOptions();
		TransparencyChessboardOptions chessboardOptions = options.getEditorOptions().getTransparencyChessboardOptions();
		ZoomOptions zoomOptions = options.getEditorOptions().getZoomOptions();

		VerticalLayout root = VerticalLayout.create(0);

		VerticalLayout imagesLayout = VerticalLayout.create();
		root.add(LabeledLayout.create(ImagesLocalize.mainPageBorderTitle(), imagesLayout));

		CheckBox showGridLines = CheckBox.create(ImagesLocalize.showGridLines());
		imagesLayout.add(showGridLines);
		propertyBuilder.add(showGridLines, gridOptions::isShowDefault, it -> gridOptions.setOption(GridOptions.ATTR_SHOW_DEFAULT, it));

		VerticalLayout gridOptionsPanel = VerticalLayout.create(0);
		gridOptionsPanel.addBorder(BorderPosition.LEFT, BorderStyle.EMPTY, null, 24);
		imagesLayout.add(gridOptionsPanel);

		IntBox gridLineZoomLimit = IntBox.create();
		gridLineZoomLimit.setRange(2, 8);
		gridOptionsPanel.add(LabeledBuilder.sided(ImagesLocalize.showGridZoomLimit(), gridLineZoomLimit));
		propertyBuilder.add(gridLineZoomLimit, gridOptions::getLineZoomFactor, it -> gridOptions.setOption(GridOptions.ATTR_LINE_ZOOM_FACTOR, it));

		IntBox gridLineSpan = IntBox.create();
		gridLineSpan.setRange(1, 100);
		gridOptionsPanel.add(LabeledBuilder.sided(ImagesLocalize.showGridEvery(), gridLineSpan));
		propertyBuilder.add(gridLineSpan, gridOptions::getLineSpan, it -> gridOptions.setOption(GridOptions.ATTR_LINE_SPAN, it));

		CheckBox showChessboard = CheckBox.create(ImagesLocalize.showTransparencyChessboard());
		imagesLayout.add(showChessboard);
		propertyBuilder.add(showChessboard, chessboardOptions::isShowDefault, it -> chessboardOptions.setOption(TransparencyChessboardOptions.ATTR_SHOW_DEFAULT, it));

		VerticalLayout chessboardPanel = VerticalLayout.create(0);
		chessboardPanel.addBorder(BorderPosition.LEFT, BorderStyle.EMPTY, null, 24);
		imagesLayout.add(chessboardPanel);

		IntBox chessboardSize = IntBox.create();
		chessboardSize.setRange(1, 100);
		chessboardPanel.add(LabeledBuilder.sided(ImagesLocalize.chessboardCellSize(), chessboardSize));
		propertyBuilder.add(chessboardSize, chessboardOptions::getCellSize, it -> chessboardOptions.setOption(TransparencyChessboardOptions.ATTR_CELL_SIZE, it));

		Platform platform = Platform.current();

		CheckBox zoomWheel = CheckBox.create(ImagesLocalize.enableMousewheelZooming(platform.os().isMac() ? "Cmd" : "Ctrl"));
		imagesLayout.add(zoomWheel);
		propertyBuilder.add(zoomWheel, zoomOptions::isWheelZooming, it -> zoomOptions.setOption(ZoomOptions.ATTR_WHEEL_ZOOMING, it));

		CheckBox smartWheel = CheckBox.create(ImagesLocalize.smartZoom());
		imagesLayout.add(smartWheel);
		propertyBuilder.add(smartWheel, zoomOptions::isSmartZooming, it -> zoomOptions.setOption(ZoomOptions.ATTR_SMART_ZOOMING, it));

		VerticalLayout smartWheelPanel = VerticalLayout.create(0);
		smartWheelPanel.addBorder(BorderPosition.LEFT, BorderStyle.EMPTY, null, 24);
		imagesLayout.add(smartWheelPanel);

		IntBox smartZoomingWidth = IntBox.create();
		smartZoomingWidth.setRange(1, 9999);
		smartWheelPanel.add(LabeledBuilder.sided(ImagesLocalize.settingsPrefferedSmartZoomWidth(), smartZoomingWidth));
		propertyBuilder.add(smartZoomingWidth, () -> zoomOptions.getPrefferedSize().getWidth(), it -> zoomOptions.setOption(ZoomOptions.ATTR_PREFFERED_WIDTH, it));

		IntBox smartZoomingHeight = IntBox.create();
		smartZoomingHeight.setRange(1, 9999);
		smartWheelPanel.add(LabeledBuilder.sided(ImagesLocalize.settingsPrefferedSmartZoomHeight(), smartZoomingHeight));
		propertyBuilder.add(smartZoomingHeight, () -> zoomOptions.getPrefferedSize().getHeight(), it -> zoomOptions.setOption(ZoomOptions.ATTR_PREFFERED_HEIGHT, it));

		if(platform.isDesktop())
		{
			VerticalLayout externalEditorLayout = VerticalLayout.create();

			FileChooserTextBoxBuilder fileChooserTextBoxBuilder = FileChooserTextBoxBuilder.create(null);
			fileChooserTextBoxBuilder.uiDisposable(myDisposable);
			fileChooserTextBoxBuilder.dialogTitle(ImagesBundle.message("select.external.executable.title"));
			fileChooserTextBoxBuilder.dialogDescription(ImagesBundle.message("select.external.executable.message"));

			FileChooserTextBoxBuilder.Controller controller = fileChooserTextBoxBuilder.build();

			propertyBuilder.add(controller::getValue, controller::setValue, () ->
					options.getExternalEditorOptions().getExecutablePath(), it ->
					options.getExternalEditorOptions().setOption(ExternalEditorOptions.ATTR_EXECUTABLE_PATH, it));

			externalEditorLayout.add(LabeledBuilder.filled(ImagesLocalize.externalEditorExecutablePath(), controller.getComponent()));

			root.add(LabeledLayout.create(ImagesLocalize.externalEditorBorderTitle(), externalEditorLayout));
		}
		return root;
	}

	@RequiredUIAccess
	@Override
	protected void disposeUIResources(@Nonnull LayoutWrapper component)
	{
		super.disposeUIResources(component);

		if(myDisposable != null)
		{
			Disposer.dispose(myDisposable);
		}
	}

	@Nonnull
	@Override
	public String getId()
	{
		return "images";
	}
}

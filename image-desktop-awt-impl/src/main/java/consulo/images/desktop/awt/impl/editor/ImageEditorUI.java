/*
 * Copyright 2004-2005 Alexey Efimov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package consulo.images.desktop.awt.impl.editor;

import consulo.annotation.access.RequiredReadAction;
import consulo.application.Application;
import consulo.application.ui.wm.IdeFocusManager;
import consulo.colorScheme.event.EditorColorsListener;
import consulo.dataContext.DataContext;
import consulo.dataContext.DataManager;
import consulo.dataContext.DataProvider;
import consulo.disposer.Disposable;
import consulo.ide.impl.idea.ide.util.DeleteHandler;
import consulo.images.ImageFileType;
import consulo.images.localize.ImagesLocalize;
import consulo.language.editor.refactoring.ui.CopyPasteDelegator;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.project.Project;
import consulo.ui.Size2D;
import consulo.ui.ex.CopyPasteSupport;
import consulo.ui.ex.CopyProvider;
import consulo.ui.ex.CutProvider;
import consulo.ui.ex.DeleteProvider;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.action.ActionManager;
import consulo.ui.ex.action.ActionPopupMenu;
import consulo.ui.ex.action.ActionToolbar;
import consulo.ui.ex.awt.*;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.ui.style.StyleManager;
import consulo.util.dataholder.Key;
import consulo.util.lang.ObjectUtil;
import consulo.util.lang.StringUtil;
import consulo.util.lang.lazy.LazyValue;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.intellij.images.ImageDocument;
import org.intellij.images.ImageDocument.ScaledImageProvider;
import org.intellij.images.editor.ImageEditor;
import org.intellij.images.editor.ImageZoomModel;
import org.intellij.images.editor.actionSystem.ImageEditorActions;
import org.intellij.images.options.*;
import org.intellij.images.thumbnail.actionSystem.ThumbnailViewActions;
import org.intellij.images.ui.ImageComponentDecorator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Image editor UI
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
final class ImageEditorUI extends JPanel implements DataProvider, CopyProvider, ImageComponentDecorator, Disposable {
    private static final String IMAGE_PANEL = "image";
    private static final String ERROR_PANEL = "error";
    private static final String ZOOM_FACTOR_PROP = "ImageEditor.zoomFactor";
    private static final String MOUSE_COORDS_EMPTY = " ";

    @Nullable
    private final ImageEditor editor;
    private final DeleteProvider deleteProvider;
    private final CopyPasteSupport copyPasteSupport;

    private final ImageZoomModel zoomModel = new ImageZoomModelImpl();
    private final MouseCursorPositionAdapter mouseCursorPositionAdapter = new MouseCursorPositionAdapter();
    private final ImageWheelAdapter wheelAdapter = new ImageWheelAdapter();
    private final ChangeListener changeListener = new DocumentChangeListener();
    private final ImageComponent imageComponent = new ImageComponent();
    private final JPanel contentPanel;
    private final JLabel mouseCoordsLabel;
    private final JLabel infoLabel;

    private final PropertyChangeListener optionsChangeListener = new OptionsChangeListener();
    private final JScrollPane myScrollPane;

    ImageEditorUI(@Nonnull Application application, @Nullable ImageEditor editor) {
        this.editor = editor;

        imageComponent.addPropertyChangeListener(ZOOM_FACTOR_PROP, e -> imageComponent.setZoomFactor(getZoomModel().getZoomFactor()));
        Options options = OptionsManager.getInstance().getOptions();
        options.addPropertyChangeListener(optionsChangeListener);

        copyPasteSupport = editor != null ? new CopyPasteDelegator(editor.getProject(), this) {
            @Nonnull
            @Override
            protected PsiElement[] getSelectedElements() {
                DataContext dataContext = DataManager.getInstance().getDataContext(ImageEditorUI.this);
                return ObjectUtil.notNull(dataContext.getData(PsiElement.KEY_OF_ARRAY), PsiElement.EMPTY_ARRAY);
            }
        } : null;
        deleteProvider = new DeleteHandler.DefaultDeleteProvider();

        ImageDocument document = imageComponent.getDocument();
        document.addChangeListener(changeListener);

        // Set options
        updateComponentOptions(options);

        // Create layout
        ImageContainerPane view = new ImageContainerPane(imageComponent);
        view.addMouseListener(new EditorMouseAdapter());
        view.addMouseListener(new FocusRequester());

        myScrollPane = ScrollPaneFactory.createScrollPane(view, true);
        myScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        myScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Zoom by wheel listener
        myScrollPane.addMouseWheelListener(wheelAdapter);
        imageComponent.addMouseMotionListener(mouseCursorPositionAdapter);
        imageComponent.addMouseListener(mouseCursorPositionAdapter);

        // Construct UI
        setLayout(new BorderLayout());

        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup)actionManager.getAction(ImageEditorActions.GROUP_TOOLBAR);
        ActionToolbar actionToolbar = actionManager.createActionToolbar(ImageEditorActions.ACTION_PLACE, actionGroup, true);
        actionToolbar.setTargetComponent(this);

        JComponent toolbarPanel = actionToolbar.getComponent();
        toolbarPanel.addMouseListener(new FocusRequester());

        JLabel errorLabel = new JBLabel(
            ImagesLocalize.errorBrokenImageFileFormat().get(),
            UIUtil.getErrorIcon(),
            SwingConstants.CENTER
        );

        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.add(errorLabel, BorderLayout.CENTER);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(myScrollPane, IMAGE_PANEL);
        contentPanel.add(errorPanel, ERROR_PANEL);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolbarPanel, BorderLayout.WEST);
        infoLabel = new JLabel((String)null, SwingConstants.RIGHT);
        infoLabel.setBorder(JBUI.Borders.emptyRight(2));
        topPanel.add(infoLabel, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        mouseCoordsLabel = new JLabel(MOUSE_COORDS_EMPTY, SwingConstants.RIGHT);
        mouseCoordsLabel.setBorder(JBUI.Borders.emptyRight(2));
        mouseCoordsLabel.setOpaque(false);
        mouseCoordsLabel.putClientProperty("FlatLaf.styleClass", "monospaced");
        bottomPanel.add(mouseCoordsLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        myScrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateZoomFactor();
            }
        });

        application.getMessageBus().connect(this)
            .subscribe(EditorColorsListener.class, editorColorsScheme -> updateComponentOptions(options));

        updateInfo();
    }

    private void updateInfo() {
        ImageDocument document = imageComponent.getDocument();
        BufferedImage image = document.getValue();
        if (image != null) {
            ColorModel colorModel = image.getColorModel();
            String format = document.getFormat();
            if (format == null) {
                format = editor != null ? ImagesLocalize.unknownFormat().get() : "";
            }
            else {
                format = format.toUpperCase(Locale.ENGLISH);
            }
            VirtualFile file = editor != null ? editor.getFile() : null;
            infoLabel.setText(
                ImagesLocalize.imageInfo(
                    image.getWidth(),
                    image.getHeight(),
                    format,
                    colorModel.getPixelSize(),
                    file != null ? StringUtil.formatFileSize(file.getLength()) : ""
                ).get()
            );
        }
        else {
            infoLabel.setText(null);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    JComponent getContentComponent() {
        return contentPanel;
    }

    ImageComponent getImageComponent() {
        return imageComponent;
    }

    @Override
    public void dispose() {
        Options options = OptionsManager.getInstance().getOptions();
        options.removePropertyChangeListener(optionsChangeListener);

        myScrollPane.removeMouseWheelListener(wheelAdapter);
        imageComponent.removeMouseMotionListener(mouseCursorPositionAdapter);
        imageComponent.removeMouseListener(mouseCursorPositionAdapter);
        imageComponent.getDocument().removeChangeListener(changeListener);

        removeAll();
    }

    @Override
    public void setTransparencyChessboardVisible(boolean visible) {
        imageComponent.getTransparencyChessboard().setVisible(visible);
        repaint();
    }

    @Override
    public boolean isTransparencyChessboardVisible() {
        return imageComponent.getTransparencyChessboard().isVisible();
    }

    @Override
    public boolean isEnabledForActionPlace(String place) {
        // Disable for thumbnails action
        return !ThumbnailViewActions.ACTION_PLACE.equals(place);
    }

    @Override
    public void setGridVisible(boolean visible) {
        imageComponent.getGrid().setVisible(visible);
        repaint();
    }

    @Override
    public boolean isGridVisible() {
        return imageComponent.getGrid().isVisible();
    }

    @Override
    public ImageZoomModel getZoomModel() {
        return zoomModel;
    }

    public void setImageProvider(ScaledImageProvider imageProvider, String format) {
        ImageDocument document = imageComponent.getDocument();
        document.setValue(imageProvider);
        if (imageProvider == null) {
            return;
        }
        document.setFormat(format);

        if (!zoomModel.isZoomLevelChanged()) {
            Options options = OptionsManager.getInstance().getOptions();
            ZoomOptions zoomOptions = options.getEditorOptions().getZoomOptions();

            if (!(zoomOptions.isSmartZooming() && updateZoomFactor())) {
                zoomModel.setZoomFactor(1.0);
            }
        }
    }

    private boolean updateZoomFactor() {
        Options options = OptionsManager.getInstance().getOptions();
        ZoomOptions zoomOptions = options.getEditorOptions().getZoomOptions();

        if (zoomOptions.isSmartZooming() && !zoomModel.isZoomLevelChanged()) {
            Double smartZoomFactor = getSmartZoomFactor(zoomOptions);
            if (smartZoomFactor != null) {
                zoomModel.setZoomFactor(smartZoomFactor);
                return true;
            }
        }
        return false;
    }

    private final class ImageContainerPane extends JBLayeredPane {
        private final ImageComponent imageComponent;

        public ImageContainerPane(ImageComponent imageComponent) {
            this.imageComponent = imageComponent;
            add(imageComponent);

            putClientProperty(
                Magnificator.CLIENT_PROPERTY_KEY,
                (Magnificator)(scale, at) -> {
                    Point locationBefore = imageComponent.getLocation();
                    ImageZoomModel model = editor != null ? editor.getZoomModel() : getZoomModel();
                    double factor = model.getZoomFactor();
                    model.setZoomFactor(scale * factor);
                    return new Point(
                        ((int)((at.x - Math.max(scale > 1.0 ? locationBefore.x : 0, 0)) * scale)),
                        ((int)((at.y - Math.max(scale > 1.0 ? locationBefore.y : 0, 0)) * scale))
                    );
                }
            );
        }

        private void centerComponents() {
            Rectangle bounds = getBounds();
            Point point = imageComponent.getLocation();
            point.x = (bounds.width - imageComponent.getWidth()) / 2;
            point.y = (bounds.height - imageComponent.getHeight()) / 2;
            imageComponent.setLocation(point);
        }

        @Override
        public void invalidate() {
            centerComponents();
            super.invalidate();
        }

        @Override
        public Dimension getPreferredSize() {
            return imageComponent.getSize();
        }

        @Override
        protected void paintComponent(@Nonnull Graphics g) {
            super.paintComponent(g);
            if (StyleManager.get().getCurrentStyle().isDark()) {
                g.setColor(UIUtil.getControlColor().brighter());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    private final class ImageWheelAdapter implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            Options options = OptionsManager.getInstance().getOptions();
            EditorOptions editorOptions = options.getEditorOptions();
            ZoomOptions zoomOptions = editorOptions.getZoomOptions();
            if (zoomOptions.isWheelZooming() && e.isControlDown()) {
                int rotation = e.getWheelRotation();
                double oldZoomFactor = zoomModel.getZoomFactor();
                Point oldPosition = myScrollPane.getViewport().getViewPosition();

                if (rotation < 0) {
                    zoomModel.zoomOut();
                }
                else if (rotation > 0) {
                    zoomModel.zoomIn();
                }

                // reset view, otherwise view size is not obtained correctly sometimes
                Component view = myScrollPane.getViewport().getView();
                myScrollPane.setViewport(null);
                myScrollPane.setViewportView(view);

                if (oldZoomFactor > 0 && rotation != 0) {
                    Point mousePoint = e.getPoint();
                    double zoomChange = zoomModel.getZoomFactor() / oldZoomFactor;
                    Point newPosition = new Point(
                        (int)Math.max(0, (oldPosition.getX() + mousePoint.getX()) * zoomChange - mousePoint.getX()),
                        (int)Math.max(0, (oldPosition.getY() + mousePoint.getY()) * zoomChange - mousePoint.getY())
                    );
                    myScrollPane.getViewport().setViewPosition(newPosition);
                }

                e.consume();
            }
        }
    }

    private final class MouseCursorPositionAdapter extends MouseAdapter {
        @Override
        public void mouseExited(@Nonnull MouseEvent e) {
            update(-1, -1);
        }

        @Override
        public void mouseMoved(@Nonnull MouseEvent e) {
            update(e.getX(), e.getY());
        }

        private void update(int mouseX, int mouseY) {
            BufferedImage image = imageComponent.getDocument().getValue();
            Dimension size = imageComponent.getCanvasSize();
            Rectangle ir = new Rectangle(ImageComponent.IMAGE_INSETS, ImageComponent.IMAGE_INSETS, size.width, size.height);
            if (ir.contains(mouseX, mouseY)) {
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();
                double x = (mouseX - ir.getX()) / size.width * imageWidth;
                double y = (mouseY - ir.getY()) / size.height * imageHeight;
                x = Math.round(x * 1000) / 1000d;
                y = Math.round(y * 1000) / 1000d;

                mouseCoordsLabel.setText(ImagesLocalize.mouseCoordinates(x, y).get());
            }
            else {
                mouseCoordsLabel.setText(MOUSE_COORDS_EMPTY);
            }
        }
    }

    private class ImageZoomModelImpl implements ImageZoomModel {
        private boolean myZoomLevelChanged;
        private final Supplier<Double> IMAGE_MAX_ZOOM_FACTOR = LazyValue.notNull(new LazyValue<Double>() {
            @Override
            public Double get() {
                if (editor == null) {
                    return Double.MAX_VALUE;
                }
                VirtualFile file = editor.getFile();

                FileType fileType = file.getFileType();
                if (fileType instanceof ImageFileType imageFileType) {
                    return imageFileType.getImageMaxZoomFactor(file, editor.getComponent());
                }
                return Double.MAX_VALUE;
            }
        });

        private double zoomFactor = 0.0d;

        @Override
        public double getZoomFactor() {
            return zoomFactor;
        }

        @Override
        public void setZoomFactor(double zoomFactor) {
            double oldZoomFactor = getZoomFactor();

            if (Double.compare(oldZoomFactor, zoomFactor) == 0) {
                return;
            }
            this.zoomFactor = zoomFactor;

            // Change current size
            updateImageComponentSize();

            revalidate();
            repaint();
            myZoomLevelChanged = false;

            imageComponent.firePropertyChange(ZOOM_FACTOR_PROP, oldZoomFactor, zoomFactor);
        }

        private double getMaximumZoomFactor() {
            double factor = IMAGE_MAX_ZOOM_FACTOR.get();
            return Math.min(factor, ZOOM_UPPER_LIMIT);
        }

        private double getMinimumZoomFactor() {
            Rectangle bounds = imageComponent.getDocument().getBounds();
            double factor = bounds != null ? 1.0d / bounds.getWidth() : 0.0d;
            return Math.max(factor, ZOOM_LOWER_LIMIT);
        }

        @Override
        public void zoomFitToWindow() {
            setZoomFactor(getZoomFitToWindow());
            myZoomLevelChanged = true;
        }

        @Override
        public void zoomIn() {
            setZoomFactor(getNextZoomIn());
            myZoomLevelChanged = true;
        }

        @Override
        public void zoomOut() {
            setZoomFactor(getNextZoomOut());
            myZoomLevelChanged = true;
        }

        private double getZoomFitToWindow() {
            Rectangle bounds = getValidImageBounds();
            if (bounds == null) {
                return 1.0;
            }

            Dimension canvasSize = getValidCanvasSize();
            if (canvasSize == null) {
                return 1.0;
            }

            return Math.min((double)canvasSize.width / bounds.width, (double)canvasSize.height / bounds.height);
        }

        private double getNextZoomIn() {
            double factor = getZoomFactor() * ZOOM_RATIO;
            return Math.min(factor, getMaximumZoomFactor());
        }

        private double getNextZoomOut() {
            double factor = getZoomFactor() / ZOOM_RATIO;
            return Math.max(factor, getMinimumZoomFactor());
        }

        @Override
        public boolean canZoomFitToWindow() {
            Rectangle bounds = getValidImageBounds();
            if (bounds == null) {
                return false;
            }

            Dimension canvasSize = getValidCanvasSize();
            return canvasSize != null
                && canvasSize.width != Math.round(bounds.width * zoomFactor)
                && canvasSize.height != Math.round(bounds.height * zoomFactor);
        }

        @Override
        public boolean canZoomIn() {
            return getZoomFactor() < getMaximumZoomFactor();
        }

        @Override
        public boolean canZoomOut() {
            // Ignore small differences caused by floating-point arithmetic.
            return getZoomFactor() - 1.0e-14 > getMinimumZoomFactor();
        }

        @Override
        public void setZoomLevelChanged(boolean value) {
            myZoomLevelChanged = value;
        }

        @Override
        public boolean isZoomLevelChanged() {
            return myZoomLevelChanged;
        }
    }

    @Nullable
    private Double getSmartZoomFactor(@Nonnull ZoomOptions zoomOptions) {
        Rectangle bounds = getValidImageBounds();
        if (bounds == null) {
            return null;
        }
        int width = bounds.width, height = bounds.height;

        Size2D preferredMinimumSize = zoomOptions.getPrefferedSize();
        if (width < preferredMinimumSize.width() && height < preferredMinimumSize.height()) {
            double factor = (preferredMinimumSize.width() / (double)width +
                preferredMinimumSize.height() / (double)height) / 2.0d;
            return Math.ceil(factor);
        }

        Dimension canvasSize = getValidCanvasSize();
        if (canvasSize == null) {
            return null;
        }

        if (canvasSize.width < width || canvasSize.height < height) {
            return Math.min(
                (double)canvasSize.height / height,
                (double)canvasSize.width / width
            );
        }

        return 1.0d;
    }

    private Rectangle getValidImageBounds() {
        Rectangle bounds = imageComponent.getDocument().getBounds();
        return bounds == null || bounds.width == 0 || bounds.height == 0 ? null : bounds;
    }

    private Dimension getValidCanvasSize() {
        Dimension canvasSize = myScrollPane.getViewport().getExtentSize();
        canvasSize.height -= ImageComponent.IMAGE_INSETS * 2;
        canvasSize.width -= ImageComponent.IMAGE_INSETS * 2;
        if (canvasSize.width <= 0 || canvasSize.height <= 0) {
            return null;
        }
        return canvasSize;
    }

    private void updateImageComponentSize() {
        Rectangle bounds = imageComponent.getDocument().getBounds();
        if (bounds != null) {
            double zoom = getZoomModel().getZoomFactor();
            imageComponent.setCanvasSize((int)Math.ceil(bounds.width * zoom), (int)Math.ceil(bounds.height * zoom));
        }
    }

    private class DocumentChangeListener implements ChangeListener {
        @Override
        public void stateChanged(@Nonnull ChangeEvent e) {
            updateImageComponentSize();

            ImageDocument document = imageComponent.getDocument();
            BufferedImage value = document.getValue();

            CardLayout layout = (CardLayout)contentPanel.getLayout();
            layout.show(contentPanel, value != null ? IMAGE_PANEL : ERROR_PANEL);

            updateInfo();

            revalidate();
            repaint();
        }
    }

    private class FocusRequester extends MouseAdapter {
        @Override
        public void mousePressed(@Nonnull MouseEvent e) {
            IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(
                () -> IdeFocusManager.getGlobalInstance().requestFocus(ImageEditorUI.this, true)
            );
        }
    }

    private static final class EditorMouseAdapter extends PopupHandler {
        @Override
        public void invokePopup(Component comp, int x, int y) {
            // Single right click
            ActionManager actionManager = ActionManager.getInstance();
            ActionGroup actionGroup = (ActionGroup)actionManager.getAction(ImageEditorActions.GROUP_POPUP);
            ActionPopupMenu menu = actionManager.createActionPopupMenu(ImageEditorActions.ACTION_PLACE, actionGroup);
            JPopupMenu popupMenu = menu.getComponent();
            popupMenu.pack();
            popupMenu.show(comp, x, y);
        }
    }

    @Nullable
    @Override
    @RequiredReadAction
    public Object getData(@Nonnull Key<?> dataId) {
        if (Project.KEY == dataId) {
            return editor != null ? editor.getProject() : null;
        }
        else if (VirtualFile.KEY == dataId) {
            return editor != null ? editor.getFile() : null;
        }
        else if (VirtualFile.KEY_OF_ARRAY == dataId) {
            return editor != null ? new VirtualFile[]{editor.getFile()} : VirtualFile.EMPTY_ARRAY;
        }
        else if (PsiFile.KEY == dataId) {
            return findPsiFile();
        }
        else if (PsiElement.KEY == dataId) {
            return findPsiFile();
        }
        else if (PsiElement.KEY_OF_ARRAY == dataId) {
            PsiElement psi = findPsiFile();
            return psi != null ? new PsiElement[]{psi} : PsiElement.EMPTY_ARRAY;
        }
        else if (CopyProvider.KEY == dataId && copyPasteSupport != null) {
            return this;
        }
        else if (CutProvider.KEY == dataId && copyPasteSupport != null) {
            return copyPasteSupport.getCutProvider();
        }
        else if (DeleteProvider.KEY == dataId) {
            return deleteProvider;
        }
        else if (ImageComponentDecorator.DATA_KEY == dataId) {
            return editor != null ? editor : this;
        }

        return null;
    }

    @Nullable
    @RequiredReadAction
    private PsiFile findPsiFile() {
        VirtualFile file = editor != null ? editor.getFile() : null;
        return file != null && file.isValid() ? PsiManager.getInstance(editor.getProject()).findFile(file) : null;
    }

    @Override
    public void performCopy(@Nonnull DataContext dataContext) {
        ImageDocument document = imageComponent.getDocument();
        BufferedImage image = document.getValue();
        CopyPasteManager.getInstance().setContents(new ImageTransferable(image));
    }

    @Override
    public boolean isCopyEnabled(@Nonnull DataContext dataContext) {
        return true;
    }

    @Override
    public boolean isCopyVisible(@Nonnull DataContext dataContext) {
        return true;
    }

    private static class ImageTransferable implements Transferable {
        private final BufferedImage myImage;

        public ImageTransferable(@Nonnull BufferedImage image) {
            myImage = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
            return DataFlavor.imageFlavor.equals(dataFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException {
            if (!DataFlavor.imageFlavor.equals(dataFlavor)) {
                throw new UnsupportedFlavorException(dataFlavor);
            }
            return myImage;
        }
    }

    private class OptionsChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Options options = (Options)evt.getSource();

            updateComponentOptions(options);
        }
    }

    private void updateComponentOptions(Options options) {
        EditorOptions editorOptions = options.getEditorOptions();
        TransparencyChessboardOptions chessboardOptions = editorOptions.getTransparencyChessboardOptions();
        GridOptions gridOptions = editorOptions.getGridOptions();

        ImageComponent.Chessboard chessboard = imageComponent.getTransparencyChessboard();
        chessboard.setCellSize(chessboardOptions.getCellSize());
        chessboard.setWhiteColor(TargetAWT.to(chessboardOptions.getWhiteColor()));
        chessboard.setBlackColor(TargetAWT.to(chessboardOptions.getBlackColor()));

        ImageComponent.Grid grid = imageComponent.getGrid();
        grid.setLineZoomFactor(gridOptions.getLineZoomFactor());
        grid.setLineSpan(gridOptions.getLineSpan());
        grid.setLineColor(TargetAWT.to(gridOptions.getLineColor()));
    }
}

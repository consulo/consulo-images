/*
 * Copyright 2000-2009 JetBrains s.r.o.
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

import consulo.images.localize.ImagesLocalize;
import consulo.ui.ex.awt.JBUI.ScaleContext;
import consulo.util.collection.Lists;
import jakarta.annotation.Nullable;
import org.intellij.images.ImageDocument;
import org.intellij.images.options.GridOptions;
import org.intellij.images.options.TransparencyChessboardOptions;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import static consulo.ui.ex.awt.JBUI.ScaleType.OBJ_SCALE;

/**
 * Image component is draw image box with effects.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
public class ImageComponent extends JComponent {
    public static final int IMAGE_INSETS = 2;

    public static final String TRANSPARENCY_CHESSBOARD_CELL_SIZE_PROP = "TransparencyChessboard.cellSize";
    public static final String TRANSPARENCY_CHESSBOARD_WHITE_COLOR_PROP = "TransparencyChessboard.whiteColor";
    public static final String TRANSPARENCY_CHESSBOARD_BLACK_COLOR_PROP = "TransparencyChessboard.blackColor";
    private static final String TRANSPARENCY_CHESSBOARD_VISIBLE_PROP = "TransparencyChessboard.visible";
    private static final String GRID_LINE_ZOOM_FACTOR_PROP = "Grid.lineZoomFactor";
    private static final String GRID_LINE_SPAN_PROP = "Grid.lineSpan";
    private static final String GRID_LINE_COLOR_PROP = "Grid.lineColor";
    private static final String GRID_VISIBLE_PROP = "Grid.visible";
    private static final String FILE_SIZE_VISIBLE_PROP = "FileSize.visible";
    private static final String FILE_NAME_VISIBLE_PROP = "FileName.visible";

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ImageComponentUI";

    private final ImageDocument document = new ImageDocumentImpl(this);
    private final Grid grid = new Grid();
    private final Chessboard chessboard = new Chessboard();
    private boolean myFileSizeVisible = true;
    private boolean myFileNameVisible = true;
    private double zoomFactor = 1d;

    public ImageComponent() {
        updateUI();
    }

    public ImageDocument getDocument() {
        return document;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public Chessboard getTransparencyChessboard() {
        return chessboard;
    }

    public Grid getGrid() {
        return grid;
    }

    public boolean isFileSizeVisible() {
        return myFileSizeVisible;
    }

    public void setFileSizeVisible(boolean fileSizeVisible) {
        boolean oldValue = myFileSizeVisible;
        myFileSizeVisible = fileSizeVisible;
        firePropertyChange(FILE_SIZE_VISIBLE_PROP, oldValue, fileSizeVisible);
    }

    public boolean isFileNameVisible() {
        return myFileNameVisible;
    }

    public void setFileNameVisible(boolean fileNameVisible) {
        boolean oldValue = myFileNameVisible;
        myFileNameVisible = fileNameVisible;
        firePropertyChange(FILE_NAME_VISIBLE_PROP, oldValue, fileNameVisible);
    }

    @Nullable
    public String getDescription() {
        BufferedImage image = getDocument().getValue();
        if (image != null) {
            return ImagesLocalize.iconDimensions(image.getWidth(), image.getHeight(), image.getColorModel().getPixelSize()).get();
        }
        return null;
    }

    public void setCanvasSize(int width, int height) {
        setSize(width + IMAGE_INSETS * 2, height + IMAGE_INSETS * 2);
    }

    public void setCanvasSize(Dimension dimension) {
        setCanvasSize(dimension.width, dimension.height);
    }

    public Dimension getCanvasSize() {
        Dimension size = getSize();
        return new Dimension(size.width - IMAGE_INSETS * 2, size.height - IMAGE_INSETS * 2);
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    @Override
    public void updateUI() {
        setUI(ImageComponentUI.createUI(this));
    }

    private static class ImageDocumentImpl implements ImageDocument {
        private final List<ChangeListener> listeners = Lists.newLockFreeCopyOnWriteList();
        private CachedScaledImageProvider imageProvider;
        private String format;
        private Image renderer;
        private final Component myComponent;
        private final ScaleContext.Cache<Rectangle> cachedBounds = new ScaleContext.Cache<>((ctx) ->
        {
            BufferedImage image = getValue(ctx.getScale(OBJ_SCALE));
            return image != null ? new Rectangle(image.getWidth(), image.getHeight()) : null;
        });

        public ImageDocumentImpl(Component component) {
            myComponent = component;
            myComponent.addPropertyChangeListener(e -> {
                if (e.getPropertyName().equals("ancestor") && e.getNewValue() == null && imageProvider != null) {
                    imageProvider.clearCache();
                }
            });
        }

        @Override
        public Image getRenderer() {
            return renderer;
        }

        @Override
        public Image getRenderer(double scale) {
            return getValue(scale);
        }

        @Nullable
        @Override
        public Rectangle getBounds(double scale) {
            ScaleContext ctx = ScaleContext.create(myComponent);
            ctx.update(OBJ_SCALE.of(scale));
            return cachedBounds.getOrProvide(ctx);
        }

        @Override
        public BufferedImage getValue() {
            return getValue(1d);
        }

        @Override
        public BufferedImage getValue(double scale) {
            return imageProvider != null ? imageProvider.apply(scale, myComponent) : null;
        }

        @Override
        public void setValue(BufferedImage image) {
            this.renderer = image != null ? Toolkit.getDefaultToolkit().createImage(image.getSource()) : null;
            setValue(image != null ? (scale, anchor) -> image : null);
        }

        @Override
        public void setValue(ScaledImageProvider imageProvider) {
            this.imageProvider = imageProvider instanceof CachedScaledImageProvider cachedScaledImageProvider
                ? cachedScaledImageProvider
                : imageProvider != null ? imageProvider::apply : null;

            cachedBounds.clear();
            fireChangeEvent(new ChangeEvent(this));
        }

        @Override
        public String getFormat() {
            return format;
        }

        @Override
        public void setFormat(String format) {
            this.format = format;
            fireChangeEvent(new ChangeEvent(this));
        }

        private void fireChangeEvent(ChangeEvent e) {
            for (ChangeListener listener : listeners) {
                listener.stateChanged(e);
            }
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
            listeners.add(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            listeners.remove(listener);
        }
    }

    public final class Chessboard {
        private int cellSize = TransparencyChessboardOptions.DEFAULT_CELL_SIZE;
        private Color whiteColor = TransparencyChessboardOptions.DEFAULT_WHITE_COLOR;
        private Color blackColor = TransparencyChessboardOptions.DEFAULT_BLACK_COLOR;
        private boolean visible = false;

        public int getCellSize() {
            return cellSize;
        }

        public void setCellSize(int cellSize) {
            int oldValue = this.cellSize;
            if (oldValue == cellSize) {
                return;
            }
            this.cellSize = cellSize;
            firePropertyChange(TRANSPARENCY_CHESSBOARD_CELL_SIZE_PROP, oldValue, cellSize);
        }

        public Color getWhiteColor() {
            return whiteColor;
        }

        public void setWhiteColor(Color whiteColor) {
            Color oldValue = this.whiteColor;
            if (Objects.equals(oldValue, whiteColor)) {
                return;
            }
            this.whiteColor = whiteColor;
            firePropertyChange(TRANSPARENCY_CHESSBOARD_WHITE_COLOR_PROP, oldValue, whiteColor);
        }

        public Color getBlackColor() {
            return blackColor;
        }

        public void setBlackColor(Color blackColor) {
            Color oldValue = this.blackColor;
            if (Objects.equals(oldValue, blackColor)) {
                return;
            }
            this.blackColor = blackColor;
            firePropertyChange(TRANSPARENCY_CHESSBOARD_BLACK_COLOR_PROP, oldValue, blackColor);
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            boolean oldValue = this.visible;
            if (oldValue == visible) {
                return;
            }
            this.visible = visible;
            firePropertyChange(TRANSPARENCY_CHESSBOARD_VISIBLE_PROP, oldValue, visible);
        }

        private Chessboard() {
        }
    }

    public final class Grid {
        private int lineZoomFactor = GridOptions.DEFAULT_LINE_ZOOM_FACTOR;
        private int lineSpan = GridOptions.DEFAULT_LINE_SPAN;
        private Color lineColor;
        private boolean visible = false;

        public int getLineZoomFactor() {
            return lineZoomFactor;
        }

        public void setLineZoomFactor(int lineZoomFactor) {
            int oldValue = this.lineZoomFactor;
            if (oldValue == lineZoomFactor) {
                return;
            }
            this.lineZoomFactor = lineZoomFactor;
            firePropertyChange(GRID_LINE_ZOOM_FACTOR_PROP, oldValue, lineZoomFactor);
        }

        public int getLineSpan() {
            return lineSpan;
        }

        public void setLineSpan(int lineSpan) {
            int oldValue = this.lineSpan;
            if (oldValue == lineSpan) {
                return;
            }
            this.lineSpan = lineSpan;
            firePropertyChange(GRID_LINE_SPAN_PROP, oldValue, lineSpan);
        }

        public Color getLineColor() {
            return lineColor;
        }

        public void setLineColor(Color lineColor) {
            Color oldValue = grid.getLineColor();
            if (Objects.equals(oldValue, lineColor)) {
                return;
            }
            this.lineColor = lineColor;
            firePropertyChange(GRID_LINE_COLOR_PROP, oldValue, lineColor);
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            boolean oldValue = this.visible;
            if (oldValue == visible) {
                return;
            }
            this.visible = visible;
            firePropertyChange(GRID_VISIBLE_PROP, oldValue, visible);
        }

        public Grid() {
        }
    }
}

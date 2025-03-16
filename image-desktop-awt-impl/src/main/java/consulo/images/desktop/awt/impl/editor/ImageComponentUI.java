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

/**
 * $Id$
 */

package consulo.images.desktop.awt.impl.editor;

import consulo.ui.ex.awt.UIUtil;
import consulo.ui.ex.awt.paint.LinePainter2D;
import org.intellij.images.ImageDocument;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * UI for {@link ImageComponent}.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
public class ImageComponentUI extends ComponentUI {
    private BufferedImage pattern;

    private ImageComponentUI(JComponent c) {
        c.addPropertyChangeListener(evt -> {
            String name = evt.getPropertyName();
            if (ImageComponent.TRANSPARENCY_CHESSBOARD_BLACK_COLOR_PROP.equals(name)
                || ImageComponent.TRANSPARENCY_CHESSBOARD_WHITE_COLOR_PROP.equals(name)
                || ImageComponent.TRANSPARENCY_CHESSBOARD_CELL_SIZE_PROP.equals(name)) {
                pattern = null;
            }
        });
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        ImageComponent ic = (ImageComponent)c;
        if (ic != null) {
            ImageDocument document = ic.getDocument();
            BufferedImage image = document.getValue(ic.getZoomFactor());
            if (image != null) {
                if (ic.isFileSizeVisible()) {
                    paintBorder(g, ic);
                }

                Dimension size = ic.getCanvasSize();
                Graphics igc = g.create(ImageComponent.IMAGE_INSETS, ImageComponent.IMAGE_INSETS, size.width, size.height);

                // Transparency chessboard
                if (ic.isTransparencyChessboardVisible() && image.getTransparency() != Transparency.OPAQUE) {
                    paintChessboard(igc, ic);
                }

                paintImage(igc, ic);

                // Grid
                if (ic.isGridVisible()) {
                    paintGrid(igc, ic);
                }

                igc.dispose();
            }
        }
    }

    private static void paintBorder(Graphics g, ImageComponent ic) {
        Dimension size = ic.getSize();
        g.setColor(ic.getTransparencyChessboardBlackColor());
        g.drawRect(0, 0, size.width - 1, size.height - 1);
    }

    private void paintChessboard(Graphics g, ImageComponent ic) {
        Dimension size = ic.getCanvasSize();
        // Create pattern
        int cellSize = ic.getTransparencyChessboardCellSize();
        int patternSize = 2 * cellSize;

        if (pattern == null) {
            pattern = UIUtil.createImage(g, patternSize, patternSize, BufferedImage.TYPE_INT_ARGB);
            Graphics imageGraphics = pattern.getGraphics();
            imageGraphics.setColor(ic.getTransparencyChessboardWhiteColor());
            imageGraphics.fillRect(0, 0, patternSize, patternSize);
            imageGraphics.setColor(ic.getTransparencyChessboardBlackColor());
            imageGraphics.fillRect(0, cellSize, cellSize, cellSize);
            imageGraphics.fillRect(cellSize, 0, cellSize, cellSize);
        }

        ((Graphics2D)g).setPaint(new TexturePaint(pattern, new Rectangle(0, 0, patternSize, patternSize)));
        g.fillRect(0, 0, size.width, size.height);
    }

    private static void paintImage(Graphics g, ImageComponent ic) {
        ImageDocument document = ic.getDocument();
        Dimension size = ic.getCanvasSize();

        Graphics2D g2d = (Graphics2D)g;
        RenderingHints oldHints = g2d.getRenderingHints();

        BufferedImage image = document.getValue(ic.getZoomFactor());
        if (image != null) {
            if (size.width > image.getWidth() && size.height > image.getHeight()) {
                // disable any kind of source image manipulation when resizing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            }
            else {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
            UIUtil.drawImage(g, image, new Rectangle(0, 0, size.width, size.height), ic);
        }

        g2d.setRenderingHints(oldHints);
    }

    private static void paintGrid(Graphics g, ImageComponent ic) {
        Dimension size = ic.getCanvasSize();
        BufferedImage image = ic.getDocument().getValue();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        double zoomX = (double)size.width / imageWidth;
        double zoomY = (double)size.height / imageHeight;
        double zoomFactor = (zoomX + zoomY) / 2.0d;
        if (zoomFactor >= ic.getGridLineZoomFactor()) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setColor(ic.getGridLineColor());
            int ls = ic.getGridLineSpan();
            for (int dx = ls; dx < imageWidth; dx += ls) {
                int x1 = (int)((double)dx * zoomX);
                LinePainter2D.paint(g2d, x1, 0, x1, size.height);
            }
            for (int dy = ls; dy < imageHeight; dy += ls) {
                int y1 = (int)((double)dy * zoomY);
                LinePainter2D.paint(g2d, 0, y1, size.width, y1);
            }
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new ImageComponentUI(c);
    }
}

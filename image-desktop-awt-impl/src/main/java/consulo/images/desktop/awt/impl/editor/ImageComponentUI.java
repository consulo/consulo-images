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
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static consulo.ui.ex.awt.paint.LinePainter2D.StrokeType.CENTERED_CAPS_SQUARE;

/**
 * UI for {@link ImageComponent}.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
public class ImageComponentUI extends ComponentUI {
    @Override
    public void paint(Graphics g, JComponent c) {
        ImageComponent ic = (ImageComponent)c;
        if (ic == null) {
            return;
        }

        ImageDocument document = ic.getDocument();
        BufferedImage image = document.getValue(ic.getZoomFactor());
        if (image == null) {
            return;
        }

        paintBorder(g, ic);

        Dimension size = ic.getCanvasSize();
        Graphics2D igc = (Graphics2D)g.create(ImageComponent.IMAGE_INSETS, ImageComponent.IMAGE_INSETS, size.width, size.height);

        // Transparency chessboard
        if (image.getTransparency() != Transparency.OPAQUE) {
            paintChessboard(igc, ic);
        }

        paintImage(igc, ic);

        paintGrid(igc, ic);

        igc.dispose();
    }

    private static void paintBorder(Graphics g, ImageComponent ic) {
        if (!ic.isFileSizeVisible()) {
            return;
        }

        Dimension size = ic.getSize();
        g.setColor(ic.getTransparencyChessboard().getBlackColor());
        g.drawRect(0, 0, size.width - 1, size.height - 1);
    }

    private void paintChessboard(Graphics2D g2d, ImageComponent ic) {
        ImageComponent.Chessboard chessboard = ic.getTransparencyChessboard();
        if (!chessboard.isVisible()) {
            return;
        }

        Dimension size = ic.getCanvasSize();
        int canvasW = size.width, canvasH = size.height;

        AffineTransform transform = g2d.getTransform();
        double scaleX = transform.getScaleX(), scaleY = transform.getScaleY();
        double cellW = Math.round(chessboard.getCellSize() * scaleX) / scaleX;
        double cellH = Math.round(chessboard.getCellSize() * scaleY) / scaleY;

        RenderingHints oldHints = g2d.getRenderingHints();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g2d.setColor(chessboard.getWhiteColor());
        g2d.fillRect(0, 0, canvasW, canvasH);

        g2d.setColor(chessboard.getBlackColor());
        Rectangle2D.Double cellRect = new Rectangle2D.Double();
        for (double x = 0; x < canvasW; x += cellW + cellW) {
            for (double y = 0; y < canvasH; y += cellH + cellH) {
                cellRect.setRect(x, y, cellW, cellH);
                g2d.fill(cellRect);
                cellRect.setRect(x + cellW, y + cellH, cellW, cellH);
                g2d.fill(cellRect);
            }
        }

        g2d.setRenderingHints(oldHints);
    }

    private static void paintImage(Graphics2D g2d, ImageComponent ic) {
        ImageDocument document = ic.getDocument();
        BufferedImage image = document.getValue(ic.getZoomFactor());
        if (image == null) {
            return;
        }

        Dimension size = ic.getCanvasSize();
        RenderingHints oldHints = g2d.getRenderingHints();
        if (size.width > image.getWidth() && size.height > image.getHeight()) {
            // disable any kind of source image manipulation when resizing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }
        else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        UIUtil.drawImage(g2d, image, new Rectangle(0, 0, size.width, size.height), ic);
        g2d.setRenderingHints(oldHints);
    }

    @SuppressWarnings("UseJBColor")
    private static void paintGrid(Graphics g, ImageComponent ic) {
        ImageComponent.Grid grid = ic.getGrid();
        if (!grid.isVisible()) {
            return;
        }

        Dimension size = ic.getCanvasSize();
        BufferedImage image = ic.getDocument().getValue();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        double zoomX = (double)size.width / imageWidth;
        double zoomY = (double)size.height / imageHeight;
        double zoomFactor = (zoomX + zoomY) / 2.0d;

        if (zoomFactor < grid.getLineZoomFactor()) {
            return;
        }

        Graphics2D g2d = (Graphics2D)g;
        int gridLineRGB = grid.getLineColor().getRGB() & 0xFFFFFF;
        Color auxColor = new Color(gridLineRGB | 0x26000000, true);
        Color mainColor = new Color(gridLineRGB | 0x4D000000, true);
        int ls = grid.getLineSpan();
        for (int dx = 1; dx < imageWidth; dx++) {
            boolean mainLine = (dx % ls) == 0;
            g.setColor(mainLine ? mainColor : auxColor);

            double x = (double)dx * zoomX;
            LinePainter2D.paint(g2d, x, 0, x, size.height, CENTERED_CAPS_SQUARE, 0.5);
        }
        for (int dy = 1; dy < imageHeight; dy++) {
            boolean mainLine = (dy % ls) == 0;
            g.setColor(mainLine ? mainColor : auxColor);

            double y = (double)dy * zoomY;
            LinePainter2D.paint(g2d, 0, y, size.width, y, CENTERED_CAPS_SQUARE, 0.5);
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return new ImageComponentUI();
    }

    private ImageComponentUI() {
    }
}

/*
 * Copyright 2000-2012 JetBrains s.r.o.
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

import consulo.images.localize.ImagesLocalize;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.ex.JBColor;
import consulo.ui.ex.awt.UIUtil;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import org.intellij.images.ImageDocument;
import consulo.ui.image.Image;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * UI for {@link ThumbnailComponent}.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
public class ThumbnailComponentUI extends ComponentUI {
    private static final String DOTS = "...";
    private static final String THUMBNAIL_COMPONENT_ERROR_STRING = "ThumbnailComponent.errorString";

    private static final Color LINE_COLOR = new Color(0x8E, 0xA8, 0xCE);
    private static final Color PNG_COLOR = new Color(0x80, 0x00, 0x80);
    private static final Color GIF_COLOR = new Color(0x00, 0x80, 0x00);
    private static final Color JPG_COLOR = new Color(0x80, 0x80, 0x00);
    private static final Color BMP_COLOR = new Color(0x00, 0x00, 0x80);

    private static final ThumbnailComponentUI ui = new ThumbnailComponentUI();

    private static final Image THUMBNAIL_BLANK = Image.empty(Image.DEFAULT_ICON_SIZE);

    static {
        UIManager.getDefaults().put(THUMBNAIL_COMPONENT_ERROR_STRING, ImagesLocalize.thumbnailsComponentErrorText().get());
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        ThumbnailComponent tc = (ThumbnailComponent)c;
        if (tc != null) {
            paintBackground(g, tc);

            if (tc.isDirectory()) {
                paintDirectory(g, tc);
            }
            else {
                paintImageThumbnail(g, tc);
            }

            // File name
            paintFileName(g, tc);
        }
    }

    private void paintDirectory(Graphics g, ThumbnailComponent tc) {
        // Paint directory icon
        TargetAWT.to(PlatformIconGroup.nodesFolder()).paintIcon(tc, g, 5, 5);

        int imagesCount = tc.getImagesCount();
        if (imagesCount > 0) {
            LocalizeValue title = ImagesLocalize.iconsCount(imagesCount);

            Font font = getSmallFont();
            FontMetrics fontMetrics = g.getFontMetrics(font);
            g.setColor(JBColor.BLACK);
            g.setFont(font);
            g.drawString(
                title.get(),
                5 + (PlatformIconGroup.nodesFolder().getWidth() - fontMetrics.stringWidth(title.get())) / 2,
                PlatformIconGroup.nodesFolder().getHeight() / 2 + fontMetrics.getAscent()
            );
        }
    }

    private void paintImageThumbnail(Graphics g, ThumbnailComponent tc) {
        // Paint blank
        TargetAWT.to(THUMBNAIL_BLANK).paintIcon(tc, g, 5, 5);

        ImageComponent imageComponent = tc.getImageComponent();
        ImageDocument document = imageComponent.getDocument();
        BufferedImage image = document.getValue();
        if (image != null) {
            paintImage(g, tc);
        }
        else {
            paintError(g, tc);
        }

        paintFileSize(g, tc);
    }

    private void paintBackground(Graphics g, ThumbnailComponent tc) {
        Dimension size = tc.getSize();
        g.setColor(tc.getBackground());
        g.fillRect(0, 0, size.width, size.height);
    }

    private void paintImage(Graphics g, ThumbnailComponent tc) {
        ImageComponent imageComponent = tc.getImageComponent();
        BufferedImage image = imageComponent.getDocument().getValue();

        int blankHeight = THUMBNAIL_BLANK.getHeight();

        // Paint image info (and reduce height of text from available height)
        blankHeight -= paintImageCaps(g, image);
        // Paint image format (and reduce height of text from available height)
        blankHeight -= paintFormatText(tc, g);

        // Paint image
        paintThumbnail(g, imageComponent, blankHeight);
    }

    private int paintImageCaps(Graphics g, BufferedImage image) {
        LocalizeValue description =
            ImagesLocalize.iconDimensions(image.getWidth(), image.getHeight(), image.getColorModel().getPixelSize());

        Font font = getSmallFont();
        FontMetrics fontMetrics = g.getFontMetrics(font);
        g.setColor(JBColor.BLACK);
        g.setFont(font);
        g.drawString(description.get(), 8, 7 + fontMetrics.getAscent());

        return fontMetrics.getHeight();
    }

    private int paintFormatText(ThumbnailComponent tc, Graphics g) {
        Font font = getSmallFont().deriveFont(Font.BOLD);
        FontMetrics fontMetrics = g.getFontMetrics(font);

        String format = tc.getFormat().toUpperCase();
        int stringWidth = fontMetrics.stringWidth(format);
        int x = THUMBNAIL_BLANK.getWidth() - stringWidth + 2;
        int y = THUMBNAIL_BLANK.getHeight() - fontMetrics.getHeight() + 4;
        g.setColor(LINE_COLOR);
        g.drawLine(x - 3, y - 1, x + stringWidth + 1, y - 1);
        g.drawLine(x - 4, y, x - 4, y + fontMetrics.getHeight() - 1);
        g.setColor(getFormatColor(format));
        g.setFont(font);
        g.drawString(
            format,
            x,
            y + fontMetrics.getAscent()
        );

        return fontMetrics.getHeight();
    }

    private Color getFormatColor(String format) {
        if ("PNG".equals(format)) {
            return PNG_COLOR;
        }
        else if ("GIF".equals(format)) {
            return GIF_COLOR;
        }
        else if ("JPG".equals(format) || "JPEG".equals(format)) {
            return JPG_COLOR;
        }
        else if ("BMP".equals(format) || "WBMP".equals(format)) {
            return BMP_COLOR;
        }
        return JBColor.BLACK;
    }

    private void paintThumbnail(Graphics g, ImageComponent imageComponent, int blankHeight) {

        // Zoom image by available size
        int maxWidth = THUMBNAIL_BLANK.getWidth() - 10;
        int maxHeight = blankHeight - 10;

        BufferedImage image = imageComponent.getDocument().getValue();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        if (imageWidth > maxWidth || imageHeight > maxHeight) {
            if (imageWidth > maxWidth) {
                double proportion = (double)maxWidth / (double)imageWidth;
                imageWidth = maxWidth;
                imageHeight = (int)((double)imageHeight * proportion);
            }
            if (imageHeight > maxHeight) {
                double proportion = (double)maxHeight / (double)imageHeight;
                imageHeight = maxHeight;
                imageWidth = (int)((double)imageWidth * proportion);
            }
        }

        imageComponent.setCanvasSize(imageWidth, imageHeight);
        Dimension size = imageComponent.getSize();

        int x = 5 + (THUMBNAIL_BLANK.getWidth() - size.width) / 2;
        int y = 5 + (THUMBNAIL_BLANK.getHeight() - size.height) / 2;

        imageComponent.paint(g.create(x, y, size.width, size.height));
    }

    private void paintFileName(Graphics g, ThumbnailComponent tc) {
        Font font = UIUtil.getLabelFont();
        FontMetrics fontMetrics = g.getFontMetrics(font);

        g.setFont(font);
        g.setColor(tc.getForeground());

        String fileName = tc.getFileName();
        String title = fileName;
        while (fontMetrics.stringWidth(title) > THUMBNAIL_BLANK.getWidth() - 8) {
            title = title.substring(0, title.length() - 1);
        }

        if (fileName.equals(title)) {
            // Center
            g.drawString(
                fileName,
                6 + (THUMBNAIL_BLANK.getWidth() - 2 - fontMetrics.stringWidth(title)) / 2,
                THUMBNAIL_BLANK.getHeight() + 8 + fontMetrics.getAscent()
            );
        }
        else {
            int dotsWidth = fontMetrics.stringWidth(DOTS);
            while (fontMetrics.stringWidth(title) > THUMBNAIL_BLANK.getWidth() - 8 - dotsWidth) {
                title = title.substring(0, title.length() - 1);
            }
            g.drawString(title + DOTS, 6, THUMBNAIL_BLANK.getHeight() + 8 + fontMetrics.getAscent());
        }
    }

    private void paintFileSize(Graphics g, ThumbnailComponent tc) {
        Font font = getSmallFont();
        FontMetrics fontMetrics = g.getFontMetrics(font);
        g.setColor(JBColor.BLACK);
        g.setFont(font);
        g.drawString(
            tc.getFileSizeText(),
            8,
            THUMBNAIL_BLANK.getHeight() + 4 - fontMetrics.getHeight() + fontMetrics.getAscent()
        );
    }

    private void paintError(Graphics g, ThumbnailComponent tc) {
        Font font = getSmallFont();
        FontMetrics fontMetrics = g.getFontMetrics(font);

        TargetAWT.to(UIUtil.getErrorIcon()).paintIcon(
            tc,
            g,
            5 + (THUMBNAIL_BLANK.getWidth() - UIUtil.getErrorIcon().getWidth()) / 2,
            5 + (THUMBNAIL_BLANK.getHeight() - UIUtil.getErrorIcon().getHeight()) / 2
        );

        // Error
        String error = getSubmnailComponentErrorString();
        g.setColor(JBColor.RED);
        g.setFont(font);
        g.drawString(error, 8, 8 + fontMetrics.getAscent());
    }

    private String getSubmnailComponentErrorString() {
        return UIManager.getString(THUMBNAIL_COMPONENT_ERROR_STRING);
    }

    private static Font getSmallFont() {
        Font labelFont = UIUtil.getLabelFont();
        return labelFont.deriveFont(labelFont.getSize2D() - 2.0f);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Font labelFont = UIUtil.getLabelFont();
        FontMetrics fontMetrics = c.getFontMetrics(labelFont);
        return new Dimension(
            THUMBNAIL_BLANK.getWidth() + 10,
            THUMBNAIL_BLANK.getHeight() + fontMetrics.getHeight() + 15
        );
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        return ui;
    }
}


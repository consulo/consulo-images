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

import consulo.annotation.component.ServiceImpl;
import consulo.application.Application;
import consulo.images.ui.EmbeddedImageViewFactory;
import consulo.ui.Component;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.intellij.images.options.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Image viewer manager implementation.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
@Singleton
@ServiceImpl
public final class ImageEditorManagerImpl implements EmbeddedImageViewFactory {
    private final Application myApplication;

    @Inject
    public ImageEditorManagerImpl(Application application) {
        myApplication = application;
    }

    @Nonnull
    @Override
    public Component createViewer(@Nonnull Image uiImage) {
        Icon icon = TargetAWT.to(uiImage);

        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration()
            .createCompatibleImage(w, h, Transparency.TRANSLUCENT);
        Graphics2D g = image.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        return TargetAWT.wrap(createImageEditorUI(myApplication, image));
    }

    @Nonnull
    public static ImageEditorUI createImageEditorUI(Application application, BufferedImage image) {
        ImageEditorUI ui = new ImageEditorUI(application, null);
        Options options = OptionsManager.getInstance().getOptions();
        EditorOptions editorOptions = options.getEditorOptions();
        GridOptions gridOptions = editorOptions.getGridOptions();
        TransparencyChessboardOptions transparencyChessboardOptions = editorOptions.getTransparencyChessboardOptions();
        ui.getImageComponent().setGridVisible(gridOptions.isShowDefault());
        ui.getImageComponent().setTransparencyChessboardVisible(transparencyChessboardOptions.isShowDefault());
        ui.setImageProvider((scale, ancestor) -> image, null);
        return ui;
    }
}

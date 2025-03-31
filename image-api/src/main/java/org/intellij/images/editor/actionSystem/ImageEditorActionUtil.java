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
package org.intellij.images.editor.actionSystem;

import consulo.ui.ex.action.AnActionEvent;
import org.intellij.images.editor.ImageZoomModel;
import org.intellij.images.ui.ImageComponentDecorator;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Editor actions utility.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 * @author UNV
 */
public final class ImageEditorActionUtil {
    public static void acceptImageDecorator(AnActionEvent e, Consumer<ImageComponentDecorator> consumer) {
        ImageComponentDecorator decorator = getValidDecorator(e);
        if (decorator != null) {
            consumer.accept(decorator);
        }
    }

    public static void acceptZoomModel(AnActionEvent e, Consumer<ImageZoomModel> consumer) {
        ImageComponentDecorator decorator = getValidDecorator(e);
        if (decorator != null) {
            consumer.accept(decorator.getZoomModel());
        }
    }

    public static boolean testImageDecorator(AnActionEvent e, Predicate<ImageComponentDecorator> predicate) {
        return testImageDecorator(e, predicate, false);
    }
    
    public static boolean testImageDecorator(AnActionEvent e, Predicate<ImageComponentDecorator> predicate, boolean defaultValue) {
        ImageComponentDecorator decorator = getValidDecorator(e);
        return decorator != null ? predicate.test(decorator) : defaultValue;
    }

    public static boolean testZoomModel(AnActionEvent e, Predicate<ImageZoomModel> predicate) {
        return testZoomModel(e, predicate, false);
    }

    public static boolean testZoomModel(AnActionEvent e, Predicate<ImageZoomModel> predicate, boolean defaultValue) {
        ImageComponentDecorator decorator = getValidDecorator(e);
        return decorator != null ? predicate.test(decorator.getZoomModel()) : defaultValue;
    }

    private static ImageComponentDecorator getValidDecorator(AnActionEvent e) {
        ImageComponentDecorator decorator = e.getData(ImageComponentDecorator.DATA_KEY);
        return decorator != null && decorator.isEnabledForActionPlace(e.getPlace()) ? decorator : null;
    }

    private ImageEditorActionUtil() {
    }
}

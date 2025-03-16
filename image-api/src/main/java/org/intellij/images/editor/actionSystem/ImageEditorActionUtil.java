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
import org.intellij.images.ui.ImageComponentDecorator;

import jakarta.annotation.Nullable;

/**
 * Editor actions utility.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
public final class ImageEditorActionUtil {
    private ImageEditorActionUtil() {
    }

    @Nullable
    public static ImageComponentDecorator getImageComponentDecorator(AnActionEvent e) {
        return e.getData(ImageComponentDecorator.DATA_KEY);
    }
}

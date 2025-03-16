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
package consulo.images.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.util.SystemInfo;
import consulo.images.impl.index.ImageInfoIndex;
import consulo.language.editor.documentation.UnrestrictedDocumentationProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFileSystemItem;
import consulo.language.psi.stub.FileBasedIndex;
import consulo.project.DumbService;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileWithId;
import org.intellij.images.util.ImageInfo;

import jakarta.annotation.Nullable;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author spleaner
 */
@ExtensionImpl
public class ImageDocumentationProvider implements UnrestrictedDocumentationProvider {
    private static final int MAX_IMAGE_SIZE = 300;

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        final String[] result = new String[]{null};

        if (element instanceof PsiFileSystemItem && !((PsiFileSystemItem)element).isDirectory()) {
            final VirtualFile file = ((PsiFileSystemItem)element).getVirtualFile();
            if (file instanceof VirtualFileWithId && !DumbService.isDumb(element.getProject())) {
                ImageInfoIndex.processValues(file, new FileBasedIndex.ValueProcessor<ImageInfo>() {
                    public boolean process(VirtualFile file, ImageInfo value) {
                        int imageWidth = value.width();
                        int imageHeight = value.height();

                        int maxSize = Math.max(value.width(), value.height());
                        if (maxSize > MAX_IMAGE_SIZE) {
                            double scaleFactor = (double)MAX_IMAGE_SIZE / (double)maxSize;
                            imageWidth *= scaleFactor;
                            imageHeight *= scaleFactor;
                        }
                        try {
                            String path = file.getPath();
                            if (SystemInfo.isWindows) {
                                path = "/" + path;
                            }
                            final String url = new URI("file", null, path, null).toString();
                            result[0] = String.format(
                                "<html><body><img src=\"%s\" width=\"%s\" height=\"%s\"><p>%sx%s, %sbpp</p><body></html>",
                                url,
                                imageWidth,
                                imageHeight,
                                value.width(),
                                value.height(),
                                value.bpp()
                            );
                        }
                        catch (URISyntaxException e) {
                            // nothing
                        }
                        return true;
                    }
                }, element.getProject());
            }
        }

        return result[0];
    }
}

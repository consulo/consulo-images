/*
 * Copyright 2000-2013 JetBrains s.r.o.
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

package consulo.images.desktop.awt.impl;

import consulo.application.Application;
import consulo.component.extension.ExtensionPoint;
import consulo.logging.Logger;
import consulo.module.content.ProjectFileIndex;
import consulo.module.content.ProjectRootManager;
import consulo.project.Project;
import consulo.util.dataholder.Key;
import consulo.util.lang.Pair;
import consulo.util.lang.ref.SoftReference;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.intellij.images.ImageDocument.ScaledImageProvider;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Image loader utility.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
public final class IfsUtil {
    private static final Logger LOG = Logger.getInstance(IfsUtil.class);

    public static final String ICO_FORMAT = "ico";
    public static final String SVG_FORMAT = "svg";

    private static final Key<Long> TIMESTAMP_KEY = Key.create("Image.timeStamp");
    private static final Key<String> FORMAT_KEY = Key.create("Image.format");
    private static final Key<SoftReference<ScaledImageProvider>> IMAGE_PROVIDER_REF_KEY = Key.create("Image.bufferedImageProvider");

    /**
     * Load image data for file and put user data attributes into file.
     *
     * @param file File
     */
    private static void refresh(@Nonnull VirtualFile file) {
        Long loadedTimeStamp = file.getUserData(TIMESTAMP_KEY);
        SoftReference<ScaledImageProvider> imageProviderRef = file.getUserData(IMAGE_PROVIDER_REF_KEY);
        if (loadedTimeStamp == null || loadedTimeStamp != file.getTimeStamp() || SoftReference.dereference(imageProviderRef) == null) {
            try {
                file.putUserData(IMAGE_PROVIDER_REF_KEY, null);

                ExtensionPoint<ImageProcessor> point = Application.get().getExtensionPoint(ImageProcessor.class);

                point.forEachExtensionSafe(processor -> {
                    if (processor.accept(file)) {
                        try {
                            Pair<String, ScaledImageProvider> info = processor.read(file);
                            if (info != null) {
                                file.putUserData(FORMAT_KEY, info.getKey());
                                file.putUserData(IMAGE_PROVIDER_REF_KEY, new SoftReference<>(info.getValue()));
                            }
                        }
                        catch (IOException e) {
                            LOG.warn(e);
                        }
                    }
                });
            }
            finally {
                // We perform loading no more needed
                file.putUserData(TIMESTAMP_KEY, file.getTimeStamp());
            }
        }
    }

    @Nullable
    public static BufferedImage getImage(@Nonnull VirtualFile file) throws IOException {
        return getImage(file, null);
    }

    @Nullable
    public static BufferedImage getImage(@Nonnull VirtualFile file, @Nullable Component ancestor) throws IOException {
        ScaledImageProvider imageProvider = getImageProvider(file);
        if (imageProvider == null) {
            return null;
        }
        return imageProvider.apply(1d, ancestor);
    }

    @Nullable
    public static ScaledImageProvider getImageProvider(@Nonnull VirtualFile file) {
        refresh(file);
        SoftReference<ScaledImageProvider> imageProviderRef = file.getUserData(IMAGE_PROVIDER_REF_KEY);
        return SoftReference.dereference(imageProviderRef);
    }

    public static boolean isSVG(@Nullable VirtualFile file) {
        return file != null && SVG_FORMAT.equalsIgnoreCase(file.getExtension());
    }

    @Nullable
    public static String getFormat(@Nonnull VirtualFile file) {
        refresh(file);
        return file.getUserData(FORMAT_KEY);
    }

    public static String getReferencePath(Project project, VirtualFile file) {
        ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        VirtualFile sourceRoot = fileIndex.getSourceRootForFile(file);
        if (sourceRoot != null) {
            return getRelativePath(file, sourceRoot);
        }

        VirtualFile root = fileIndex.getContentRootForFile(file);
        if (root != null) {
            return getRelativePath(file, root);
        }

        return file.getPath();
    }

    private static String getRelativePath(VirtualFile file, VirtualFile root) {
        if (root.equals(file)) {
            return file.getPath();
        }
        return "/" + VirtualFileUtil.getRelativePath(file, root, '/');
    }
}

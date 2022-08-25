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
package org.intellij.images.thumbnail;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.project.Project;

import javax.annotation.Nonnull;

/**
 * Thumbnail manager.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
@ServiceAPI(ComponentScope.PROJECT)
public abstract class ThumbnailManager {

  public static ThumbnailManager getManager(final Project project) {
    return project.getInstance(ThumbnailManager.class);
  }

  /**
   * Create thumbnail view
   *
   * @return Return thumbnail view
   */
  @Nonnull
  public abstract ThumbnailView getThumbnailView();
}

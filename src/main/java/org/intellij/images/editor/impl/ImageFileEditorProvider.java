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
package org.intellij.images.editor.impl;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.dumb.DumbAware;
import consulo.fileEditor.FileEditor;
import consulo.fileEditor.FileEditorPolicy;
import consulo.fileEditor.FileEditorProvider;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Inject;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;

/**
 * Image editor provider.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
@ExtensionImpl
final class ImageFileEditorProvider implements FileEditorProvider, DumbAware {
  @NonNls
  private static final String EDITOR_TYPE_ID = "images";

  private final ImageFileTypeManager typeManager;

  @Inject
  ImageFileEditorProvider(ImageFileTypeManager typeManager) {
    this.typeManager = typeManager;
  }

  @Override
  public boolean accept(@Nonnull Project project, @Nonnull VirtualFile file) {
    return typeManager.isImage(file);
  }

  @Override
  @Nonnull
  public FileEditor createEditor(@Nonnull Project project, @Nonnull VirtualFile file) {
    return new ImageFileEditorImpl(project, file);
  }

  @Override
  public void disposeEditor(@Nonnull FileEditor editor) {
    consulo.ide.impl.idea.openapi.util.Disposer.dispose(editor);
  }

  @Override
  @Nonnull
  public String getEditorTypeId() {
    return EDITOR_TYPE_ID;
  }

  @Override
  @Nonnull
  public FileEditorPolicy getPolicy() {
    return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
  }
}

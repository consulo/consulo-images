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
package org.intellij.images.fileTypes.impl;

import com.intellij.openapi.fileTypes.FileNameMatcher;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.vfs.VirtualFile;
import consulo.images.ImageFileType;
import consulo.images.ImageFileTypeProvider;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Image file type manager.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
@Singleton
public final class ImageFileTypeManagerImpl extends ImageFileTypeManager
{
	private final Map<FileType, String> myFileTypes = new HashMap<>();

	@Inject
	public ImageFileTypeManagerImpl()
	{
		FileTypeConsumer consumer = new FileTypeConsumer()
		{
			@Override
			public void consume(@Nonnull FileType fileType)
			{
				consume(fileType, fileType.getDefaultExtension());
			}

			@Override
			public void consume(@Nonnull FileType fileType, @NonNls String extensions)
			{
				myFileTypes.put(fileType, extensions);
			}

			@Override
			public void consume(@Nonnull FileType fileType, FileNameMatcher... matchers)
			{
				throw new UnsupportedOperationException();
			}

			@Nullable
			@Override
			public FileType getStandardFileTypeByName(@Nonnull @NonNls String name)
			{
				throw new UnsupportedOperationException();
			}
		};

		for(ImageFileTypeProvider provider : ImageFileTypeProvider.EP_NAME.getExtensionList())
		{
			provider.register(consumer);
		}
	}

	@Override
	public Collection<FileType> getFileTypes()
	{
		return myFileTypes.keySet();
	}

	@Nonnull
	public Map<FileType, String> getRegisteredFileTypes()
	{
		return myFileTypes;
	}

	@Override
	public boolean isImage(@Nonnull VirtualFile file)
	{
		return file.getFileType() == ImageFileType.INSTANCE;
	}

	@Nonnull
	@Override
	public FileType getImageFileType()
	{
		return ImageFileType.INSTANCE;
	}
}

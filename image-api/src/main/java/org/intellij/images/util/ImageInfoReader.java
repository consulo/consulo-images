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
package org.intellij.images.util;

import consulo.util.io.UnsyncByteArrayInputStream;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author spleaner
 */
public class ImageInfoReader {
    private ImageInfoReader() {
    }

    @Nullable
    public static ImageInfo getInfo(@Nonnull String file) {
        return read(file);
    }

    @Nullable
    public static ImageInfo getInfo(@Nonnull byte[] data) {
        return read(data);
    }

    @Nullable
    private static ImageInfo read(@Nonnull String file) {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
            try {
                return readFileData(raf);
            }
            finally {
                try {
                    raf.close();
                }
                catch (IOException e) {
                    // nothing
                }
            }
        }
        catch (IOException e) {
            return null;
        }
    }

    @Nullable
    private static ImageInfo read(@Nonnull byte[] data) {
        DataInputStream is = new DataInputStream(new UnsyncByteArrayInputStream(data));
        try {
            return readFileData(is);
        }
        catch (IOException e) {
            return null;
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                // nothing
            }
        }
    }

    @Nullable
    private static ImageInfo readFileData(@Nonnull DataInput di) throws IOException {
        int b1 = di.readUnsignedByte();
        int b2 = di.readUnsignedByte();

        if (b1 == 0x47 && b2 == 0x49) {
            return readGif(di);
        }

        if (b1 == 0x89 && b2 == 0x50) {
            return readPng(di);
        }

        if (b1 == 0xFF && b2 == 0xD8) {
            return readJpeg(di);
        }

        //if (b1 == 0x42 && b2 == 0x4d) {
        //  return readBmp(raf);
        //}

        return null;
    }

    private static final byte[] GIF_MAGIC_87A = {0x46, 0x38, 0x37, 0x61};
    private static final byte[] GIF_MAGIC_89A = {0x46, 0x38, 0x39, 0x61};

    @Nullable
    private static ImageInfo readGif(DataInput di) throws IOException {
        byte[] a = new byte[11]; // 4 from the GIF signature + 7 from the global header

        di.readFully(a);
        if ((!eq(a, 0, GIF_MAGIC_89A, 0, 4)) && (!eq(a, 0, GIF_MAGIC_87A, 0, 4))) {
            return null;
        }

        int width = getShortLittleEndian(a, 4);
        int height = getShortLittleEndian(a, 6);

        int flags = a[8] & 0xFF;
        int bpp = ((flags >> 4) & 0x07) + 1;

        return new ImageInfo(width, height, bpp);
    }

    private static ImageInfo readBmp(RandomAccessFile raf) throws IOException {
        byte[] a = new byte[44];
        if (raf.read(a) != a.length) {
            return null;
        }

        int width = getIntLittleEndian(a, 16);
        int height = getIntLittleEndian(a, 20);

        if (width < 1 || height < 1) {
            return null;
        }

        int bpp = getShortLittleEndian(a, 26);
        if (bpp != 1 && bpp != 4 && bpp != 8 && bpp != 16 && bpp != 24 & bpp != 32) {
            return null;
        }

        return new ImageInfo(width, height, bpp);
    }

    @Nullable
    private static ImageInfo readJpeg(DataInput di) throws IOException {
        byte[] a = new byte[13];
        while (true) {
            di.readFully(a, 0, 4);

            int marker = getShortBigEndian(a, 0);
            int size = getShortBigEndian(a, 2);

            if ((marker & 0xFF00) != 0xFF00) {
                return null;
            }

            if (marker == 0xffe0) {
                if (size < 14) {
                    di.skipBytes(size - 2);
                    continue;
                }

                di.readFully(a, 0, 12);
                di.skipBytes(size - 14);
            }
            else if (marker >= 0xFFC0 && marker <= 0xFFCF && marker != 0xFFC4 && marker != 0xFFC8) {
                di.readFully(a, 0, 6);

                int bpp = (a[0] & 0xFF) * (a[5] & 0xFF);
                int width = getShortBigEndian(a, 3);
                int height = getShortBigEndian(a, 1);

                return new ImageInfo(width, height, bpp);
            }
            else {
                di.skipBytes(size - 2);
            }
        }
    }

    private static final byte[] PNG_MAGIC = {0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    @Nullable
    private static ImageInfo readPng(DataInput di) throws IOException {
        byte[] a = new byte[27];

        di.readFully(a);
        if (!eq(a, 0, PNG_MAGIC, 0, 6)) {
            return null;
        }

        int width = getIntBigEndian(a, 14);
        int height = getIntBigEndian(a, 18);
        int bpp = a[22] & 0xFF;
        int colorType = a[23] & 0xFF;
        if (colorType == 2 || colorType == 6) {
            bpp *= 3;
        }

        return new ImageInfo(width, height, bpp);
    }

    private static int getShortBigEndian(byte[] a, int offset) {
        return (a[offset] & 0xFF) << 8 | (a[offset + 1] & 0xFF);
    }

    private static boolean eq(byte[] a1, int offset1, byte[] a2, int offset2, int num) {
        while (num-- > 0) {
            if (a1[offset1++] != a2[offset2++]) {
                return false;
            }
        }

        return true;
    }

    private static int getIntBigEndian(byte[] a, int offset) {
        return (a[offset] & 0xFF) << 24 | (a[offset + 1] & 0xFF) << 16 | (a[offset + 2] & 0xFF) << 8 | a[offset + 3] & 0xFF;
    }

    private static int getIntLittleEndian(byte[] a, int offset) {
        return (a[offset + 3] & 0xFF) << 24 | (a[offset + 2] & 0xFF) << 16 | (a[offset + 1] & 0xFF) << 8 | a[offset] & 0xFF;
    }

    private static int getShortLittleEndian(byte[] a, int offset) {
        return (a[offset] & 0xFF) | (a[offset + 1] & 0xFF) << 8;
    }
}

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
package consulo.images.impl.setting;

import consulo.colorScheme.EditorColorsManager;
import consulo.images.ImageColorKeys;
import consulo.ui.color.ColorValue;
import consulo.util.xml.serializer.JDOMExternalizable;
import consulo.util.xml.serializer.JDOMExternalizer;
import org.intellij.images.options.TransparencyChessboardOptions;
import org.jdom.Element;

import jakarta.annotation.Nonnull;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

/**
 * Background options implementation.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
final class TransparencyChessboardOptionsImpl implements TransparencyChessboardOptions, JDOMExternalizable {
    private boolean showDefault = true;
    private int cellSize = DEFAULT_CELL_SIZE;
    private final PropertyChangeSupport propertyChangeSupport;

    TransparencyChessboardOptionsImpl(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
    }

    @Override
    public boolean isShowDefault() {
        return showDefault;
    }

    @Override
    public int getCellSize() {
        return cellSize;
    }

    @Nonnull
    @Override
    public ColorValue getWhiteColor() {
        ColorValue color = EditorColorsManager.getInstance().getGlobalScheme().getColor(ImageColorKeys.WHITE_CELL_COLOR_KEY);
        return Objects.requireNonNull(color, "White color required");
    }

    @Nonnull
    @Override
    public ColorValue getBlackColor() {
        ColorValue color = EditorColorsManager.getInstance().getGlobalScheme().getColor(ImageColorKeys.BLACK_CELL_COLOR_KEY);
        return Objects.requireNonNull(color, "BlackCell color required");
    }

    void setShowDefault(boolean showDefault) {
        boolean oldValue = this.showDefault;
        if (oldValue != showDefault) {
            this.showDefault = showDefault;
            propertyChangeSupport.firePropertyChange(ATTR_SHOW_DEFAULT, oldValue, this.showDefault);
        }
    }

    void setCellSize(int cellSize) {
        int oldValue = this.cellSize;
        if (oldValue != cellSize) {
            this.cellSize = cellSize;
            propertyChangeSupport.firePropertyChange(ATTR_CELL_SIZE, oldValue, this.cellSize);
        }
    }

    @Override
    public void inject(TransparencyChessboardOptions options) {
        setShowDefault(options.isShowDefault());
        setCellSize(options.getCellSize());
    }

    @Override
    public boolean setOption(String name, Object value) {
        if (ATTR_SHOW_DEFAULT.equals(name)) {
            setShowDefault((Boolean)value);
        }
        else if (ATTR_CELL_SIZE.equals(name)) {
            setCellSize((Integer)value);
        }
        else {
            return false;
        }
        return true;
    }

    @Override
    public void readExternal(Element element) {
        setShowDefault(JDOMExternalizer.readBoolean(element, ATTR_SHOW_DEFAULT));
        setCellSize(JDOMExternalizer.readInteger(element, ATTR_CELL_SIZE, DEFAULT_CELL_SIZE));
    }

    @Override
    public void writeExternal(Element element) {
        JDOMExternalizer.write(element, ATTR_SHOW_DEFAULT, showDefault);
        JDOMExternalizer.write(element, ATTR_CELL_SIZE, cellSize);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransparencyChessboardOptions)) {
            return false;
        }

        TransparencyChessboardOptions otherOptions = (TransparencyChessboardOptions)o;

        return cellSize == otherOptions.getCellSize() && showDefault == otherOptions.isShowDefault();

    }

    public int hashCode() {
        int result;
        result = (showDefault ? 1 : 0);
        result = 29 * result + cellSize;
        return result;
    }
}

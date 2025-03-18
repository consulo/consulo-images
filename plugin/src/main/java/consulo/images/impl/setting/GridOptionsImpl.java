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
import jakarta.annotation.Nonnull;
import org.intellij.images.options.GridOptions;
import org.jdom.Element;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

/**
 * Grid options implementation.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
final class GridOptionsImpl implements GridOptions, JDOMExternalizable {
    private boolean showDefault;
    private int lineMinZoomFactor = DEFAULT_LINE_ZOOM_FACTOR;
    private int lineSpan = DEFAULT_LINE_SPAN;
    private final PropertyChangeSupport propertyChangeSupport;

    GridOptionsImpl(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
    }

    @Override
    public boolean isShowDefault() {
        return showDefault;
    }

    @Override
    public int getLineZoomFactor() {
        return lineMinZoomFactor;
    }

    @Override
    public int getLineSpan() {
        return lineSpan;
    }

    @Nonnull
    @Override
    public ColorValue getLineColor() {
        ColorValue color = EditorColorsManager.getInstance().getGlobalScheme().getColor(ImageColorKeys.GRID_LINE_COLOR_KEY);
        return Objects.requireNonNull(color, "Grid line сolor required");
    }

    void setShowDefault(boolean showDefault) {
        boolean oldValue = this.showDefault;
        if (oldValue != showDefault) {
            this.showDefault = showDefault;
            propertyChangeSupport.firePropertyChange(ATTR_SHOW_DEFAULT, oldValue, this.showDefault);
        }
    }

    void setLineMinZoomFactor(int lineMinZoomFactor) {
        int oldValue = this.lineMinZoomFactor;
        if (oldValue != lineMinZoomFactor) {
            this.lineMinZoomFactor = lineMinZoomFactor;
            propertyChangeSupport.firePropertyChange(ATTR_LINE_ZOOM_FACTOR, oldValue, this.lineMinZoomFactor);
        }
    }

    void setLineSpan(int lineSpan) {
        int oldValue = this.lineSpan;
        if (oldValue != lineSpan) {
            this.lineSpan = lineSpan;
            propertyChangeSupport.firePropertyChange(ATTR_LINE_SPAN, oldValue, this.lineSpan);
        }
    }

    @Override
    public void inject(GridOptions options) {
        setShowDefault(options.isShowDefault());
        setLineMinZoomFactor(options.getLineZoomFactor());
        setLineSpan(options.getLineSpan());
    }

    @Override
    public boolean setOption(String name, Object value) {
        if (ATTR_SHOW_DEFAULT.equals(name)) {
            setShowDefault((Boolean)value);
        }
        else if (ATTR_LINE_ZOOM_FACTOR.equals(name)) {
            setLineMinZoomFactor((Integer)value);
        }
        else if (ATTR_LINE_SPAN.equals(name)) {
            setLineSpan((Integer)value);
        }
        else {
            return false;
        }
        return true;
    }

    @Override
    public void readExternal(Element element) {
        showDefault = JDOMExternalizer.readBoolean(element, ATTR_SHOW_DEFAULT);
        lineMinZoomFactor = JDOMExternalizer.readInteger(element, ATTR_LINE_ZOOM_FACTOR, DEFAULT_LINE_ZOOM_FACTOR);
        lineSpan = JDOMExternalizer.readInteger(element, ATTR_LINE_SPAN, DEFAULT_LINE_SPAN);
    }

    @Override
    public void writeExternal(Element element) {
        JDOMExternalizer.write(element, ATTR_SHOW_DEFAULT, showDefault);
        JDOMExternalizer.write(element, ATTR_LINE_ZOOM_FACTOR, lineMinZoomFactor);
        JDOMExternalizer.write(element, ATTR_LINE_SPAN, lineSpan);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
            || obj instanceof GridOptions otherOptions
            && lineMinZoomFactor == otherOptions.getLineZoomFactor()
            && lineSpan == otherOptions.getLineSpan()
            && showDefault == otherOptions.isShowDefault();
    }

    @Override
    public int hashCode() {
        int result = (showDefault ? 1 : 0);
        result = 29 * result + lineMinZoomFactor;
        result = 29 * result + lineSpan;
        return result;
    }
}

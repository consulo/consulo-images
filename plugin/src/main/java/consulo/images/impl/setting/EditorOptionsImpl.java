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

import consulo.util.xml.serializer.InvalidDataException;
import consulo.util.xml.serializer.JDOMExternalizable;
import consulo.util.xml.serializer.WriteExternalException;
import org.intellij.images.options.EditorOptions;
import org.intellij.images.options.GridOptions;
import org.intellij.images.options.TransparencyChessboardOptions;
import org.intellij.images.options.ZoomOptions;
import org.jdom.Element;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

/**
 * Editor options implementation.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
final class EditorOptionsImpl implements EditorOptions, JDOMExternalizable {
    private final GridOptions gridOptions;
    private final TransparencyChessboardOptions transparencyChessboardOptions;
    private final ZoomOptions zoomOptions;

    EditorOptionsImpl(PropertyChangeSupport propertyChangeSupport) {
        gridOptions = new GridOptionsImpl(propertyChangeSupport);
        transparencyChessboardOptions = new TransparencyChessboardOptionsImpl(propertyChangeSupport);
        zoomOptions = new ZoomOptionsImpl(propertyChangeSupport);
    }

    @Override
    public GridOptions getGridOptions() {
        return gridOptions;
    }

    @Override
    public TransparencyChessboardOptions getTransparencyChessboardOptions() {
        return transparencyChessboardOptions;
    }

    @Override
    public ZoomOptions getZoomOptions() {
        return zoomOptions;
    }

    @Override
    public EditorOptions clone() throws CloneNotSupportedException {
        return (EditorOptions)super.clone();
    }

    @Override
    public void inject(EditorOptions options) {
        gridOptions.inject(options.getGridOptions());
        transparencyChessboardOptions.inject(options.getTransparencyChessboardOptions());
        zoomOptions.inject(options.getZoomOptions());
    }

    @Override
    public boolean setOption(String name, Object value) {
        return gridOptions.setOption(name, value) ||
            transparencyChessboardOptions.setOption(name, value) ||
            zoomOptions.setOption(name, value);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        ((JDOMExternalizable)gridOptions).readExternal(element);
        ((JDOMExternalizable)transparencyChessboardOptions).readExternal(element);
        ((JDOMExternalizable)zoomOptions).readExternal(element);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        ((JDOMExternalizable)gridOptions).writeExternal(element);
        ((JDOMExternalizable)transparencyChessboardOptions).writeExternal(element);
        ((JDOMExternalizable)zoomOptions).writeExternal(element);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this
            || obj instanceof EditorOptions otherOptions
            && Objects.equals(getGridOptions(), otherOptions.getGridOptions())
            && Objects.equals(getTransparencyChessboardOptions(), otherOptions.getTransparencyChessboardOptions())
            && Objects.equals(getZoomOptions(), otherOptions.getZoomOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(gridOptions, transparencyChessboardOptions, zoomOptions);
    }
}

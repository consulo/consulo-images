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
import org.intellij.images.options.ExternalEditorOptions;
import org.intellij.images.options.Options;
import org.jdom.Element;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

/**
 * Default options implementation.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
final class OptionsImpl implements Options, JDOMExternalizable {
    /**
     * Property change support (from injection)
     */
    private final PropertyChangeSupport propertyChangeSupport;

    private final EditorOptions editorOptions;
    private final ExternalEditorOptions externalEditorOptions;

    OptionsImpl() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        editorOptions = new EditorOptionsImpl(propertyChangeSupport);
        externalEditorOptions = new ExternalEditorOptionsImpl(propertyChangeSupport);
    }

    @Override
    public EditorOptions getEditorOptions() {
        return editorOptions;
    }

    @Override
    public ExternalEditorOptions getExternalEditorOptions() {
        return externalEditorOptions;
    }

    @Override
    public void inject(Options options) {
        editorOptions.inject(options.getEditorOptions());
        externalEditorOptions.inject(options.getExternalEditorOptions());
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public boolean setOption(String name, Object value) {
        return editorOptions.setOption(name, value) || externalEditorOptions.setOption(name, value);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        ((JDOMExternalizable)editorOptions).readExternal(element);
        ((JDOMExternalizable)externalEditorOptions).readExternal(element);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        ((JDOMExternalizable)editorOptions).writeExternal(element);
        ((JDOMExternalizable)externalEditorOptions).writeExternal(element);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this
            || obj instanceof Options otherOptions
            && Objects.equals(getEditorOptions(), otherOptions.getEditorOptions())
            && Objects.equals(getExternalEditorOptions(), otherOptions.getExternalEditorOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(editorOptions, externalEditorOptions);
    }
}

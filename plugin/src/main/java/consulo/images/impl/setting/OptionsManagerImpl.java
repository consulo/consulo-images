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

import consulo.annotation.component.ServiceImpl;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.RoamingType;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.util.xml.serializer.InvalidDataException;
import consulo.util.xml.serializer.WriteExternalException;
import jakarta.inject.Singleton;
import org.intellij.images.options.Options;
import org.intellij.images.options.OptionsManager;
import org.jdom.Element;

/**
 * Options configurable manager.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
@Singleton
@ServiceImpl
@State(name = "Images.OptionsManager", storages = {@Storage(value = "images.support.xml", roamingType = RoamingType.DISABLED)})
public class OptionsManagerImpl extends OptionsManager implements PersistentStateComponent<Element> {
    private final OptionsImpl options = new OptionsImpl();

    @Override
    public Options getOptions() {
        return options;
    }

    @Override
    public Element getState() {
        Element element = new Element("state");
        try {
            options.writeExternal(element);
        }
        catch (WriteExternalException e) {
            throw new RuntimeException(e);
        }
        return element;
    }

    @Override
    public void loadState(Element state) {
        try {
            options.readExternal(state);
        }
        catch (InvalidDataException e) {
            throw new RuntimeException(e);
        }
    }
}

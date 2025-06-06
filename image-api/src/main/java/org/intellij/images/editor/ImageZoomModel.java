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
package org.intellij.images.editor;

/**
 * Location model presents bounds of image.
 * The zoom it calculated as y = exp(x/2).
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
public interface ImageZoomModel {
    int POWER_LIMIT = 10;
    double ZOOM_RATIO = Math.sqrt(2);
    double ZOOM_UPPER_LIMIT = Math.pow(ZOOM_RATIO, POWER_LIMIT);
    double ZOOM_LOWER_LIMIT = Math.pow(1 / ZOOM_RATIO, POWER_LIMIT);

    double getZoomFactor();

    void setZoomFactor(double zoomFactor);

    void zoomFitToWindow();

    void zoomIn();

    void zoomOut();

    void setZoomLevelChanged(boolean value);

    boolean canZoomFitToWindow();

    boolean canZoomIn();

    boolean canZoomOut();

    boolean isZoomLevelChanged();

    ImageZoomModel STUB = new ImageZoomModel() {
        @Override
        public double getZoomFactor() {
            return 1;
        }

        @Override
        public void setZoomFactor(double zoomFactor) {
        }

        @Override
        public void zoomFitToWindow() {
        }

        @Override
        public void zoomIn() {
        }

        @Override
        public void zoomOut() {
        }

        @Override
        public void setZoomLevelChanged(boolean value) {
        }

        @Override
        public boolean canZoomFitToWindow() {
            return false;
        }

        @Override
        public boolean canZoomIn() {
            return false;
        }

        @Override
        public boolean canZoomOut() {
            return false;
        }

        @Override
        public boolean isZoomLevelChanged() {
            return false;
        }
    };
}

/**
 * @author VISTALL
 * @since 24-Aug-22
 */
open module consulo.images {
    // TODO remove in future
    requires java.desktop;

    requires consulo.ide.api;
    requires consulo.ide.impl;

    requires com.intellij.images_image.api;
}
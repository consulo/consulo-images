/**
 * @author VISTALL
 * @since 2022-08-25
 */
module com.intellij.images_image.svg.impl {
    requires com.intellij.images_image.api;
    requires com.intellij.images_image.svg.api;

    requires com.intellij.xml;

    // TODO remove this dependency
    requires consulo.ide.impl;
}
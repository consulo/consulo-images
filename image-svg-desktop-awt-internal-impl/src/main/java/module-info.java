/**
 * @author VISTALL
 * @since 2025-03-18
 */
module com.intellij.images_svg.desktop.awt.internal.impl {
    requires com.intellij.images_image.api;
    requires com.intellij.images_image.svg.api;
    requires com.intellij.images_image.svg.impl;

    requires com.intellij.images.desktop.awt.impl;

    requires com.github.weisj.jsvg;

    requires java.desktop;
}
/**
 * @author VISTALL
 * @since 25-Aug-22
 */
module com.intellij.images_svg.desktop.awt.impl {
    requires com.intellij.images_image.api;
    requires com.intellij.images_image.svg.api;
    requires com.intellij.images_image.svg.impl;

    requires com.intellij.images.desktop.awt.impl;

    requires com.github.weisj.jsvg;

    requires consulo.library.batik;

    requires xml.apis.ext;

    requires java.desktop;
}
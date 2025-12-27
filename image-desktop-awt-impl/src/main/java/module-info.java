/**
 * @author VISTALL
 * @since 2022-08-25
 */
module com.intellij.images.desktop.awt.impl {
    requires java.desktop;

    requires consulo.ui.ex.awt.api;

    requires com.intellij.images_image.api;

    requires consulo.language.editor.api;
    requires consulo.language.editor.refactoring.api;

    // TODO remove this big dependency
    requires consulo.ide.impl;

    requires org.apache.commons.imaging;

    exports consulo.images.desktop.awt.impl to com.intellij.images_svg.desktop.awt.impl, com.intellij.images_svg.desktop.awt.internal.impl;
    exports consulo.images.desktop.awt.impl.editor to com.intellij.images_svg.desktop.awt.impl;
    exports consulo.images.desktop.awt.impl.thumbnail to com.intellij.images_svg.desktop.awt.impl;

    uses javax.imageio.spi.ImageReaderSpi;
}
/**
 * @author VISTALL
 * @since 24-Aug-22
 */
open module consulo.images {
    // TODO remove in future
    requires java.desktop;

    requires consulo.ide.api;
    requires consulo.ide.impl;
    requires consulo.language.editor.api;
    requires consulo.language.api;
    requires consulo.project.ui.view.api;
    requires consulo.ui.ex.awt.api;
    requires consulo.process.api;

    requires com.intellij.images_image.api;
}
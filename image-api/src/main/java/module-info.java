/**
 * @author VISTALL
 * @since 2022-08-25
 */
module com.intellij.images_image.api {
    requires transitive consulo.ide.api;

    // TODO remove in future
    requires java.desktop;

    exports consulo.images;
    exports consulo.images.editor;
    exports consulo.images.icon;
    exports consulo.images.localize;
    exports consulo.images.ui;
    exports org.intellij.images;
    exports org.intellij.images.editor;
    exports org.intellij.images.editor.actionSystem;
    exports org.intellij.images.fileTypes;
    exports org.intellij.images.options;
    exports org.intellij.images.thumbnail.actionSystem;
    exports org.intellij.images.thumbnail;
    exports org.intellij.images.editor.actions;
    exports org.intellij.images.thumbnail.actions;
    exports org.intellij.images.util;
    exports org.intellij.images.ui;
}
/**
 * @author VISTALL
 * @since 24-Aug-22
 */
open module consulo.images {
  // TODO remove in future
  requires java.desktop;

  requires consulo.ide.api;
  requires consulo.ide.impl;

  requires static consulo.library.batik;
  requires static com.intellij.xml;
  requires xml.apis.ext;

  exports com.intellij.util;
  exports consulo.images;
  exports consulo.images.icon;
  exports consulo.images.impl;
  exports consulo.images.localize;
  exports consulo.images.preferences.impl;
  exports consulo.images.svg;
  exports consulo.images.svg.editor;
  exports org.intellij.images;
  exports org.intellij.images.actions;
  exports org.intellij.images.completion;
  exports org.intellij.images.editor;
  exports org.intellij.images.editor.actionSystem;
  exports org.intellij.images.editor.actions;
  exports org.intellij.images.editor.impl;
  exports org.intellij.images.fileTypes;
  exports org.intellij.images.fileTypes.impl;
  exports org.intellij.images.index;
  exports org.intellij.images.options;
  exports org.intellij.images.options.impl;
  exports org.intellij.images.thumbnail;
  exports org.intellij.images.thumbnail.actionSystem;
  exports org.intellij.images.thumbnail.actions;
  exports org.intellij.images.thumbnail.impl;
  exports org.intellij.images.ui;
  exports org.intellij.images.util;
  exports org.intellij.images.vfs;
}
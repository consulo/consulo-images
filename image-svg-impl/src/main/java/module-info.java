/**
 * @author VISTALL
 * @since 25-Aug-22
 */
module com.intellij.images.image.svg.impl {
  requires transitive com.intellij.images_image.api;
  requires transitive com.intellij.xml;

  // TODO remove this dependency
  requires consulo.ide.impl;

  exports consulo.images.svg;
  
  exports consulo.images.svg.internal to com.intellij.images.svg.desktop.awt.impl;
}
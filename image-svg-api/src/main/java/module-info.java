/**
 * @author VISTALL
 * @since 25-Aug-22
 */
module com.intellij.images_image.svg.api {
  requires transitive com.intellij.images_image.api;
  requires transitive consulo.language.api;
  requires transitive com.intellij.xml;

  exports consulo.images.svg;
  exports consulo.images.svg.internal to com.intellij.images_svg.desktop.awt.impl, com.intellij.images_image.svg.impl;
}
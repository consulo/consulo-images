/**
 * @author VISTALL
 * @since 25-Aug-22
 */
module com.intellij.images.svg.desktop.awt.impl {
  requires com.intellij.images_image.api;
  requires com.intellij.images.image.svg.impl;

  requires com.intellij.images.desktop.awt.impl;

  requires consulo.library.batik;

  requires xml.apis.ext;

  requires java.desktop;
}
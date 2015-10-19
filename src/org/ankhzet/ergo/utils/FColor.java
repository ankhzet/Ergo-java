package org.ankhzet.ergo.utils;

import java.awt.Color;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FColor extends Color {

  static final int epsilon = (int) (255 * 0.01);

  public FColor(int rgb) {
    super(rgb);
  }

  public FColor(int r, int g, int b) {
    super(r, g, b);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FColor))
      return false;

    int rgb1 = getRGB();
    int rgb2 = ((FColor) obj).getRGB();

    if (rgb1 == rgb2)
      return true;

    byte c1 = (byte) ((rgb1 & 0xFF));
    byte c2 = (byte) ((rgb2 & 0xFF));
    if (epsilon <= Math.abs(c1 - c2))
      return false;

    c1 = (byte) ((rgb1 & 0xFF00) >> 8);
    c2 = (byte) ((rgb2 & 0xFF00) >> 8);
    if (epsilon <= Math.abs(c1 - c2))
      return false;

    c1 = (byte) ((rgb1 & 0xFF0000) >> 16);
    c2 = (byte) ((rgb2 & 0xFF0000) >> 16);
    if (epsilon <= Math.abs(c1 - c2))
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    return getRGB();
  }

  public int compare(FColor other) {
    int rgb1 = getRGB();
    int rgb2 = other.getRGB();

    if (rgb1 == rgb2)
      return 0;

    int delta = 0;
    byte c1 = (byte) ((rgb1 & 0xFF));
    byte c2 = (byte) ((rgb2 & 0xFF));
    delta += c1 - c2;

    c1 = (byte) ((rgb1 & 0xFF00) >> 8);
    c2 = (byte) ((rgb2 & 0xFF00) >> 8);
    delta += c1 - c2;

    c1 = (byte) ((rgb1 & 0xFF0000) >> 16);
    c2 = (byte) ((rgb2 & 0xFF0000) >> 16);
    delta += c1 - c2;

    return Integer.signum(delta / 3);
  }

  public int brightness() {
    int rgb1 = getRGB();

    int r = (byte) ((rgb1 & 0xFF));
    int g = (byte) ((rgb1 & 0xFF00) >> 8);
    int b = (byte) ((rgb1 & 0xFF0000) >> 16);

    return (r + g + b) / 3;
  }

}

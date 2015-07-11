package org.ankhzet.ergo.utils;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Utils {

  public static int constraint(int value, int min, int max) {
    if (value > max)
      value = max;
    if (value < min)
      value = min;

    return value;
  }
}

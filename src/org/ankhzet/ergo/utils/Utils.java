package org.ankhzet.ergo.utils;

import java.util.Arrays;
import java.util.List;

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

  public static <T> T[] shift(T[] array) {
    if (array.length == 0)
      return null;

    List<T> list = Arrays.asList(array);
    T o = list.remove(0);
    try {
      return list.toArray((T[]) array.getClass().newInstance());
    } catch (InstantiationException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static <T> T[] unshift(T o, T[] array) {
    List<T> list = Arrays.asList(array);
    list.add(0, o);
    try {
      return list.toArray((T[]) array.getClass().newInstance());
    } catch (InstantiationException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

}

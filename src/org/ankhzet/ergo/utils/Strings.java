package org.ankhzet.ergo.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Strings extends ArrayList<String> {

  public String join(String glue) {
    String result = "";
    boolean e = true;
    for (String s : this)
      if (!s.isEmpty()) {
        result += e ? s : glue + s;
        e = false;
      }

    return result;
  }

  public static Strings explode(String s, String regex) {
    Strings r = new Strings();
    r.addAll(Arrays.asList(s.split(regex)));
    return r;
  }

  public String pop() {
    int elements = size();
    return elements > 0 ? remove(elements - 1) : null;
  }

  public String shift() {
    return size() > 0 ? remove(0) : null;
  }
}

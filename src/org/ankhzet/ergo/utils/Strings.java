package org.ankhzet.ergo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Strings extends ArrayList<String> {

  public Strings() {
    super();
  }

  public Strings(String... strings) {
    super(Arrays.asList(strings));
  }

  public Strings(Collection<String> strings) {
    super(strings);
  }

  public static Strings explode(String s, String regex) {
    return new Strings(s.split(regex));
  }

  public static String toTitleCase(String input) {
    StringBuilder titleCase = new StringBuilder();
    boolean nextTitleCase = true;

    for (char c : input.toCharArray()) {
      if (Character.isSpaceChar(c))
        nextTitleCase = true;
      else
        if (nextTitleCase) {
          c = Character.toTitleCase(c);
          nextTitleCase = false;
        }

      titleCase.append(c);
    }

    return titleCase.toString();
  }

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

  public String pop() {
    int elements = size();
    return elements > 0 ? remove(elements - 1) : null;
  }

  public String shift() {
    return size() > 0 ? remove(0) : null;
  }

}

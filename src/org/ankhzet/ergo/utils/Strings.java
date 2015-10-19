package org.ankhzet.ergo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

  public static String md5(String str) {
    String hash16 = "";
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      byte[] digest = md5.digest(str.getBytes());
      for (int i = 0; i < digest.length; i++)
        hash16 += Integer.toString(digest[i] & 0xff, 16);
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace();
    }
    return hash16;
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


package org.ankhzet.ergo.db;

import java.util.Locale;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Table {

  public static String truncate(String table) {
    return String.format(Locale.US, "truncate table %s", table);
  }
  
  public static String delete(String table) {
    return String.format(Locale.US, "delete from %s", table);
  }
  
}

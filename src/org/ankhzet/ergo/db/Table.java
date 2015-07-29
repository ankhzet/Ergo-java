package org.ankhzet.ergo.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class Table {

  @DependencyInjection(instantiate = false)
  protected DBLayer db;

  @DependenciesInjected(suppressInherited = false, beforeInherited = false)
  private void diInjected() throws Exception {
    if (!assumeExists())
      throw new Exception(String.format("Can't create [%s] table!", tableName()));
    else
      System.out.printf("Table [%s] is OK.\n", tableName());
  }

  public abstract String tableName();

  protected abstract String schema();

  public boolean assumeExists() {
    try {
      db.createTable(schema());
      return true;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return false;
  }

  public boolean truncate() {
    try {
      PreparedStatement ps = db.prepareStatement(truncate(tableName()));
      ps.executeUpdate();
      return true;
    } catch (SQLException ex) {
      ex.printStackTrace();
    }

    return false;
  }

  public static String select(String table, String collumns) {
    return String.format(Locale.US, "select %s from %s", collumns, table);
  }

  public static String insert(String table, String collumns, String values) {
    return String.format(Locale.US, "insert into %s (%s) values (%s)", table, collumns, values);
  }

  public static String truncate(String table) {
    return String.format(Locale.US, "truncate table %s", table);
  }

  public static String delete(String table) {
    return String.format(Locale.US, "delete from %s", table);
  }

}

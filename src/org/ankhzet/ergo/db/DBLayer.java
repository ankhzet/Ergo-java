package org.ankhzet.ergo.db;

import java.io.File;
import java.sql.*;
import org.ankhzet.ergo.Config;
import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DBLayer {

  public static final String dbFileExt = "sqlite";

  @DependencyInjection()
  protected Config config;

  public Connection connection;
  public Statement statmt;
  public ResultSet resSet;
  
  @DependenciesInjected()
  private void di() throws SQLException {
    try {
      connection = dbConnection(config.appDir(config.appName()));
      System.out.println("--connected.");
    } catch (SQLException e) {
      System.out.println("--connection failed!");
      throw e;
    }
  }

  public DBLayer() throws ClassNotFoundException {
    connection = null;
    Class.forName("org.sqlite.JDBC");
  }

  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
  }

  public int createTable(String sql) throws SQLException {
    Statement s = connection.createStatement();
    return s.executeUpdate(String.format("create table if not exists %s", sql));
  }

  public static int getFetchRowCount(ResultSet rs) {
    int size = 0;
    try {
      rs.last();
      size = rs.getRow();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return size;
  }

  public int update(String table, String sql) throws SQLException {
    Statement s = connection.createStatement();
    return s.executeUpdate(String.format("update `%s` set %s", table, sql));
  }

  public ResultSet fetchQuery(String sql) throws SQLException {
//    System.out.println("Fetch: " + sql);
    PreparedStatement s = prepareStatement(sql);
    return s.executeQuery();
  }

  public static ResultSet getSingleRow(String query) {
    try {
      ResultSet rs = IoC.get(DBLayer.class).fetchQuery(query);
      if (rs.next())
        return rs;
    } catch (SQLException ex) {
    }
    return null;
  }

  @Override
  protected void finalize() throws Throwable {
    if (connection != null)
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    super.finalize();
  }

  public static Connection dbConnection(String dbname) throws SQLException {
    File f = new File(dbname);
    String fileName = f.getName();
    if (Strings.explode(fileName, "\\.").size() <= 1)
      dbname = dbname + "." + dbFileExt;

    String connection = String.format("jdbc:sqlite:%s", dbname);
    System.out.println("Connecting to [" + connection + "]...");
    return DriverManager.getConnection(connection);
  }

}

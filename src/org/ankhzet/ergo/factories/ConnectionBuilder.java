package org.ankhzet.ergo.factories;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ankhzet.ergo.Config;
import org.ankhzet.ergo.classfactory.builder.ClassBuilder;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConnectionBuilder extends ClassBuilder<Connection> {

  public static final String dbFileExt = "sqlite";

  protected Config config;

  public ConnectionBuilder(Config config) {
    this.config = config;
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException ex) {
      LOG.log(Level.SEVERE, "Failed to init JDBC driver", ex);
    }
  }

  @Override
  public synchronized Connection build(Class<? extends Connection> c, Object... args) throws Exception {
    String dbName = (args.length > 0) ? (String) args[0] : config.appDir(config.appName());

    File f = new File(dbName);
    String fileName = f.getName();
    if (Strings.explode(fileName, "\\.").size() <= 1)
      dbName = dbName + "." + dbFileExt;

    String connectionString = String.format("jdbc:sqlite:%s", dbName);

    Connection connection = null;
    try {
      connection = DriverManager.getConnection(connectionString);
      LOG.log(Level.INFO, "Connecting to [{0}]: OK", connectionString);
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, String.format("Connecting to [{0}]: FAIL", connectionString), e);
      throw e;
    }

    return connection;
  }

  private static final Logger LOG = Logger.getLogger(ConnectionBuilder.class.getName());

}

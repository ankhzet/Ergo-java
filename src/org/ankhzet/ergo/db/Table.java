package org.ankhzet.ergo.db;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.db.query.Builder;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class Table {

  @DependencyInjection(instantiate = false)
  protected Builder builder;

  @DependenciesInjected(suppressInherited = false, beforeInherited = false)
  private void diInjected() throws Exception {
    try {
      createIfNotExists();
      LOG.log(Level.FINE, "Table [{0}] is OK.\n", tableName());
    } catch (Throwable e) {
      throw new Exception(String.format("Can't create table [%s]!", tableName()), e);
    }
  }
  public void createIfNotExists() throws SQLException {
    tableBuilder().create(schema());
   }
  public void truncate() throws SQLException {
    tableBuilder().truncate();
   }
  protected Builder tableBuilder() {
    return builder.table(tableName());
   }
  public abstract String tableName();

  protected abstract String schema();

  private static final Logger LOG = Logger.getLogger(Table.class.getName());

}

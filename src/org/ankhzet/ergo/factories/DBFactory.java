package org.ankhzet.ergo.factories;

import java.sql.Connection;
import org.ankhzet.ergo.Config;
import org.ankhzet.ergo.classfactory.ClassFactory;
import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.db.DB;
import org.ankhzet.ergo.db.tables.BookmarksTable;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DBFactory extends ClassFactory<DB> {

  public DBFactory() {
    register(DB.class);

    registerClass(BookmarksTable.class);

    registerClass(Connection.class, new ConnectionBuilder(IoC.get(Config.class)));
  }

}

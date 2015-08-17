package org.ankhzet.ergo.factories;

import java.sql.Connection;
import org.ankhzet.ergo.Config;
import org.ankhzet.ergo.classfactory.ClassFactory;
import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.db.DB;
import org.ankhzet.ergo.db.query.SQLGrammar;
import org.ankhzet.ergo.db.tables.BookmarksTable;
import org.ankhzet.ergo.db.tables.MangaOptionsTable;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DBFactory extends ClassFactory<DB> {

  public DBFactory() {
    register(DB.class);

    registerClass(Connection.class, new ConnectionBuilder(IoC.get(Config.class)));
    registerClass(SQLGrammar.class);

    registerClass(BookmarksTable.class);
    registerClass(MangaOptionsTable.class);
  }

}

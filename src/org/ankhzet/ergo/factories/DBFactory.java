package org.ankhzet.ergo.factories;

import org.ankhzet.ergo.classfactory.ClassFactory;
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
  }

}

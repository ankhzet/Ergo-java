package org.ankhzet.ergo.db;

import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.db.tables.BookmarksTable;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DB extends DBLayer {

  @DependenciesInjected()
  private void diInjected() {
    IoC.get(BookmarksTable.class);
  }

  public DB() {
    super();
  }

}

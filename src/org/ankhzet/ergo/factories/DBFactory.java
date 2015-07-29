
package org.ankhzet.ergo.factories;

import org.ankhzet.ergo.classfactory.ClassFactory;
import org.ankhzet.ergo.db.DB;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DBFactory extends ClassFactory<DB> {

  public DBFactory() {
    register(DB.class);
  }

}

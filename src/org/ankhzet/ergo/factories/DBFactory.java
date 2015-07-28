
package org.ankhzet.ergo.factories;

import org.ankhzet.ergo.classfactory.ClassFactory;
import org.ankhzet.ergo.db.DBLayer;
import org.ankhzet.ergo.ui.UILogic;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DBFactory extends ClassFactory<UILogic> {

  public DBFactory() {
    register(DBLayer.class);
  }

}

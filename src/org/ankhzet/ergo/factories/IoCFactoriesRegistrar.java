package org.ankhzet.ergo.factories;

import org.ankhzet.ergo.classfactory.CommonFactoryRegistrar;
import org.ankhzet.ergo.classfactory.FactoryRegistrar;
import org.ankhzet.ergo.ui.xgui.XPathFilePicker;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoCFactoriesRegistrar {

  static FactoryRegistrar uiPages = new FactoryRegistrar(UIPageFactory.class);
  static FactoryRegistrar logics = new FactoryRegistrar(UILogicFactory.class);
  static FactoryRegistrar db = new FactoryRegistrar(DBFactory.class);

  static FactoryRegistrar filePicker = new CommonFactoryRegistrar(XPathFilePicker.class, (Class c) -> new XPathFilePicker("File pick"));

  public static void register() {
    // do nothing
  }

}

package org.ankhzet.ergo;

import org.ankhzet.ergo.classfactory.CommonFactoryRegistrar;
import org.ankhzet.ergo.factories.UIPageFactory;
import org.ankhzet.ergo.factories.UILogicFactory;
import org.ankhzet.ergo.classfactory.FactoryRegistrar;
import org.ankhzet.ergo.xgui.XPathFilePicker;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoCFactoriesRegistrar {

  static FactoryRegistrar uiPages = new FactoryRegistrar(UIPageFactory.class);
  static FactoryRegistrar logics = new FactoryRegistrar(UILogicFactory.class);

  static FactoryRegistrar filePicker = new CommonFactoryRegistrar(XPathFilePicker.class, (Class c) -> new XPathFilePicker("File pick"));

  static void register() {
    // do nothing
  }

}

package org.ankhzet.ergo.factories;

import ankh.IoC;
import ankh.factory.ClassFactory;
import org.ankhzet.ergo.App;
import org.ankhzet.ergo.Config;
import org.ankhzet.ergo.ui.UIContainerListener;
import org.ankhzet.ergo.ui.UILogic;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UILogicFactory extends ClassFactory<UILogic> {

  public UILogicFactory(IoC ioc) {
    super(ioc);
    
    register(UILogic.class);

    registerClass(UIContainerListener.class);

    registerClass(Config.class, (c, args) -> new Config(App.appName()));
  }

}

package org.ankhzet.ergo.factories;

import org.ankhzet.ergo.classfactory.ClassFactory;
import org.ankhzet.ergo.UILogic;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UILogicFactory extends ClassFactory<UILogic> {

  public UILogicFactory() {
    register(UILogic.class);

  }

}

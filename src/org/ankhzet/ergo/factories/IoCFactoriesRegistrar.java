package org.ankhzet.ergo.factories;

import java.awt.Toolkit;
import org.ankhzet.ergo.classfactory.ClassFactoryRegistrar;
import org.ankhzet.ergo.classfactory.FactoryRegistrar;
import org.ankhzet.ergo.classfactory.SingleClassFactoryRegistrar;
import org.ankhzet.ergo.ui.pages.MangaChapterPicker;
import org.ankhzet.ergo.ui.xgui.XControls;
import org.ankhzet.ergo.ui.xgui.XMessageBox;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoCFactoriesRegistrar {

  static FactoryRegistrar<?> uiPages = new ClassFactoryRegistrar<>(new UIPageFactory());
  static FactoryRegistrar<?> logics = new ClassFactoryRegistrar<>(new UILogicFactory());
  static FactoryRegistrar<?> db = new ClassFactoryRegistrar<>(new DBFactory());

  static FactoryRegistrar<?> filePicker;

  public static void register() {

    filePicker = new SingleClassFactoryRegistrar<>(MangaChapterPicker.class, c -> new MangaChapterPicker("File pick"));

  }

}

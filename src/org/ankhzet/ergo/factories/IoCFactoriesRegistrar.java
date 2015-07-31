package org.ankhzet.ergo.factories;

import java.awt.Toolkit;
import org.ankhzet.ergo.classfactory.*;
import org.ankhzet.ergo.ui.pages.home.MangaChapterPicker;
import org.ankhzet.ergo.ui.xgui.XControls;
import org.ankhzet.ergo.ui.xgui.XMessageBox;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoCFactoriesRegistrar extends ClassFactory<Object> {
  
  static IoCFactoriesRegistrar registrar;

  static FactoryRegistrar<?> uiPages = new ClassFactoryRegistrar<>(new UIPageFactory());
  static FactoryRegistrar<?> logics = new ClassFactoryRegistrar<>(new UILogicFactory());
  static FactoryRegistrar<?> db = new ClassFactoryRegistrar<>(new DBFactory());

  public IoCFactoriesRegistrar() {

    registerClass(Toolkit.class, (c, args) -> Toolkit.getDefaultToolkit());

    registerClass(MangaChapterPicker.class, (c, args) -> new MangaChapterPicker(args.length > 0 ? (String)args[0] : "File pick"));
    registerClass(XMessageBox.class);
    registerClass(XControls.class);

  }

  
  public static void register() { 
    registrar = new IoCFactoriesRegistrar();
  }

}

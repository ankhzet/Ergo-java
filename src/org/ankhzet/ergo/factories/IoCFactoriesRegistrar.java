package org.ankhzet.ergo.factories;

import ankh.IoC;
import ankh.factory.ClassFactory;
import ankh.registrar.ClassFactoryRegistrar;
import ankh.registrar.FactoryRegistrar;
import java.awt.Toolkit;
import org.ankhzet.ergo.ConfigParser;
import org.ankhzet.ergo.ui.pages.home.MangaChapterPicker;
import org.ankhzet.ergo.ui.xgui.XControls;
import org.ankhzet.ergo.ui.xgui.XMessageBox;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoCFactoriesRegistrar extends ClassFactory {

  static IoCFactoriesRegistrar registrar;

  static FactoryRegistrar<?> uiPages = new ClassFactoryRegistrar<>(new UIPageFactory(IoC.instance()));
  static FactoryRegistrar<?> logics = new ClassFactoryRegistrar<>(new UILogicFactory(IoC.instance()));
  static FactoryRegistrar<?> db = new ClassFactoryRegistrar<>(new DBFactory(IoC.instance()));

  public IoCFactoriesRegistrar(IoC ioc) {
    super(ioc);

    registerClass(Toolkit.class, (c, args) -> Toolkit.getDefaultToolkit());

    registerClass(MangaChapterPicker.class, (c, args) -> new MangaChapterPicker(args.length > 0 ? (String) args[0] : "File pick"));
    registerClass(XMessageBox.class);
    registerClass(XControls.class);

    registerClass(ConfigParser.class, (c, args) -> new ConfigParser((String) args[0]));
  }

  public static void register() {
    registrar = new IoCFactoriesRegistrar(IoC.instance());
  }

}

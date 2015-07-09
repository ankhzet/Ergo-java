package org.ankhzet.ergo.factories;

import org.ankhzet.ergo.ClassFactory.Builder.Builder;
import org.ankhzet.ergo.ClassFactory.ClassFactory;
import org.ankhzet.ergo.UIHomePage;
import org.ankhzet.ergo.UIPage;
import org.ankhzet.ergo.reader.UIReaderPage;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIPageFactory extends ClassFactory<UIPage> {
  
  public UIPageFactory() {

    register(UIHomePage.class, new Builder<UIPage>() {

      @Override
      public UIPage call() throws Exception {
        return new UIHomePage();
      }
    });

    register(UIReaderPage.class, new Builder<UIPage>() {

      @Override
      public UIPage call() throws Exception {
        return new UIReaderPage();
      }
    });

  }
}

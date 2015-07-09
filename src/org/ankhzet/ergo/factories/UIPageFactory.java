package org.ankhzet.ergo.factories;

import org.ankhzet.ergo.classfactory.ClassFactory;
import org.ankhzet.ergo.pages.UIHomePage;
import org.ankhzet.ergo.UIPage;
import org.ankhzet.ergo.reader.Reader;
import org.ankhzet.ergo.pages.UIReaderPage;
import org.ankhzet.ergo.reader.MagnifyGlass;
import org.ankhzet.ergo.reader.PageRenderOptions;
import org.ankhzet.ergo.reader.SwipeHandler;
import org.ankhzet.ergo.reader.chapter.ChapterLoader;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIPageFactory extends ClassFactory<UIPage> {
  
  public UIPageFactory() {

    register(UIHomePage.class);
    register(UIReaderPage.class);
    
    register(Reader.class);
    register(PageRenderOptions.class);
//    register(ChapterData.class);
    register(ChapterLoader.class);
    register(MagnifyGlass.class);
    register(SwipeHandler.class);

  }
}

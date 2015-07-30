package org.ankhzet.ergo.factories;

import org.ankhzet.ergo.classfactory.ClassFactory;
import org.ankhzet.ergo.manga.chapter.ChapterLoader;
import org.ankhzet.ergo.ui.pages.duplicates.UIDuplicatesPage;
import org.ankhzet.ergo.ui.pages.UIPage;
import org.ankhzet.ergo.ui.pages.home.UIHomePage;
import org.ankhzet.ergo.ui.pages.reader.UIReaderPage;
import org.ankhzet.ergo.ui.pages.reader.reader.MagnifyGlass;
import org.ankhzet.ergo.ui.pages.reader.reader.PageRenderOptions;
import org.ankhzet.ergo.ui.pages.reader.reader.Reader;
import org.ankhzet.ergo.ui.pages.reader.reader.SwipeHandler;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIPageFactory extends ClassFactory<UIPage> {

  public UIPageFactory() {

    register(UIHomePage.class);
    register(UIReaderPage.class);
    register(UIDuplicatesPage.class);

    registerClass(Reader.class);
    registerClass(PageRenderOptions.class);
//    registerClass(ChapterData.class);
    registerClass(ChapterLoader.class);
    registerClass(MagnifyGlass.class);
    registerClass(SwipeHandler.class);

  }

}

package org.ankhzet.ergo.factories;

import org.ankhzet.ergo.classfactory.ClassFactory;
import org.ankhzet.ergo.ui.pages.UIHomePage;
import org.ankhzet.ergo.ui.pages.UIPage;
import org.ankhzet.ergo.ui.pages.UIDuplicatesPage;
import org.ankhzet.ergo.ui.pages.readerpage.reader.Reader;
import org.ankhzet.ergo.ui.pages.readerpage.UIReaderPage;
import org.ankhzet.ergo.ui.pages.readerpage.reader.MagnifyGlass;
import org.ankhzet.ergo.ui.pages.readerpage.reader.PageRenderOptions;
import org.ankhzet.ergo.ui.pages.readerpage.reader.SwipeHandler;
import org.ankhzet.ergo.manga.chapter.ChapterLoader;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIPageFactory extends ClassFactory<UIPage> {

  public UIPageFactory() {

    register(UIHomePage.class);
    register(UIReaderPage.class);
    register(UIDuplicatesPage.class);

    register(Reader.class);
    register(PageRenderOptions.class);
//    register(ChapterData.class);
    register(ChapterLoader.class);
    register(MagnifyGlass.class);
    register(SwipeHandler.class);

  }

}

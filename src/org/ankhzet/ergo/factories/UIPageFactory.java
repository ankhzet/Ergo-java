package org.ankhzet.ergo.factories;

import org.ankhzet.ergo.classfactory.ClassFactory;
import org.ankhzet.ergo.manga.chapter.chaptercacher.ScansCache;
import org.ankhzet.ergo.manga.chapter.page.ReadOptions;
import org.ankhzet.ergo.ui.pages.duplicates.UIDuplicatesPage;
import org.ankhzet.ergo.ui.pages.UIPage;
import org.ankhzet.ergo.ui.pages.home.UIHomePage;
import org.ankhzet.ergo.ui.pages.reader.UIReaderPage;
import org.ankhzet.ergo.ui.pages.reader.reader.MagnifyGlass;
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
    registerClass(ReadOptions.class);
//    registerClass(ChapterData.class);
    registerClass(ScansCache.class);
    registerClass(MagnifyGlass.class);
    registerClass(SwipeHandler.class);

  }

}

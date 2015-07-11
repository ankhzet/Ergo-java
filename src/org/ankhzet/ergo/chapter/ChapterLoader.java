package org.ankhzet.ergo.chapter;

import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.ui.pages.readerpage.reader.Reader;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ChapterLoader {

  @DependencyInjection
  UILogic ui;
  @DependencyInjection
  Reader reader;

  Chapter chapter;
  Thread loader, cacher;

  @DependenciesInjected
  void dependenciesInjected() {
    layout();
  }

  public void load(Chapter chapter) {
    this.chapter = chapter;

    loader = new Thread(() -> {
      int wait = 0;
      while (reader.isBusy() && wait < 100)
        try {
          Thread.sleep(10);
          wait += 1;
        } catch (InterruptedException ex) {
        }

      if (!reader.isBusy()) {
        reader.cacheChapter(chapter, ui);
        reader.flushCache(true);
      }
    });

    loader.start();
  }

  final void layout() {
    cacher = new Thread(() -> {
      try {
        while (true) {
          if (reader.flushPending()) {
            while (reader.isBusy())
              Thread.sleep(10);

            reader.flushCache(false);
            reader.calcLayout(ui.clientArea.width, ui.clientArea.height - UILogic.UIPANEL_HEIGHT, ui);
            ui.progressDone();
          }

          Thread.sleep(10);
        }
      } catch (InterruptedException ex) {
      }
//        cacher = null;
    });

    cacher.start();
  }

}

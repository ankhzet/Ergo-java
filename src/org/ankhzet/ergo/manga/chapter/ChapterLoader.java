package org.ankhzet.ergo.manga.chapter;

import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.ui.pages.reader.reader.Reader;

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
      try {
        synkReader();
      } catch (InterruptedException ex) {
      }

      if (!reader.isBusy()) {
        reader.cacheChapter(chapter, ui);
        reader.flushCache(true);
      }
    });

    loader.start();
  }

  void layout() {
    cacher = new Thread(() -> {
      try {
        while (true) {
          if (reader.flushPending()) {
            synkReader();

            reader.flushCache(false);
            reader.calcLayout(ui.clientArea.width, ui.clientArea.height - UILogic.UIPANEL_HEIGHT, ui);
            ui.progressDone();
          }

          Thread.sleep(50);
        }
      } catch (InterruptedException ex) {
      }
    });

    cacher.start();
  }

  void synkReader() throws InterruptedException {
    while (reader.isBusy())
      Thread.sleep(50);
  }

}

package org.ankhzet.ergo.reader;

import org.ankhzet.ergo.utils.Utils;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class PageNavigator {

  private int currentPage = -1;

  public abstract int totalPages();

  public int currentPage() {
    return currentPage;
  }

  public int lastPage() {
    return totalPages() - 1;
  }

  public int toFirstPage() {
    return setPage(0);
  }

  public int toLastPage() {
    return setPage(lastPage());
  }

  public int nextPage() {
    return changePage(false);
  }

  public int prevPage() {
    return changePage(true);
  }

  public int changePage(boolean backward) {
    int delta = backward ? -1 : 1;
    return setPage(currentPage + delta);
  }

  public int setPage(int page) {
    return currentPage = Utils.constraint(page, 0, lastPage());
  }

}

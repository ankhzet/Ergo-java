package org.ankhzet.ergo.ui.pages.readerpage.reader;

import org.ankhzet.ergo.utils.Utils;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class PageNavigator {
  
  public interface NavigationListener {
    void pageSet(int requested, int set);
  }

  private int currentPage = -1;
  private NavigationListener listener;

  public void setNavListener(NavigationListener l) {
    listener = l;
  }
  
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
    currentPage = Utils.constraint(page, 0, lastPage());
    
    if (listener != null)
      listener.pageSet(page, currentPage);

    return currentPage;
  }

}

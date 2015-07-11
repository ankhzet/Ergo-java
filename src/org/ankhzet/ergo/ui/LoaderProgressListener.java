package org.ankhzet.ergo.ui;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface LoaderProgressListener {

  public final int STAGE_LOADING = 1;
  public final int STATE_CACHING = 2;
  public final int STATE_LAYOUTING = 3;
  public final String[] LABELS = {"", "Loading", "Caching", "Layouting"};

  public boolean onProgress(int state, int progress, int max);

  public void progressDone();
}

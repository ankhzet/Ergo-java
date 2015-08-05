package org.ankhzet.ergo.manga.chapter.chaptercacher.cache;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <C> Type of caheable objects, processable by task
 */
public interface CacheTask<C> {

  boolean process(C cacheable);

}

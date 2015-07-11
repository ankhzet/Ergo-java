package org.ankhzet.ergo.classfactory.builder;

import java.lang.reflect.Constructor;
import java.util.concurrent.locks.ReentrantLock;
import org.ankhzet.ergo.classfactory.exceptions.FailedFactoryProductException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Type> Class, builded by builder
 */
public class ClassBuilder<Type> implements Builder<Type> {

  ReentrantLock lock = new ReentrantLock();

  /**
   *
   * @param c Class to be instantiated
   * @return instantiated object
   * @throws Exception if has no accesible constructors, has more than 1
   * constructor or can't resolve it's dependencies.
   */
  @Override
  synchronized public Type build(Class<? extends Type> c) throws Exception {
    if (!lock.tryLock())
      return null;

    try {
      if (lock.getHoldCount() > 1)
        return null;

      Constructor<?> constructor;
      try {
        constructor = c.getConstructor();
      } catch (NoSuchMethodException | SecurityException ex) {
        throw new FailedFactoryProductException(c, new Exception("Must have default constructor"));
      }

      Type instance = (Type) constructor.newInstance();

      return instance;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean isBuilding() {
    return lock.isLocked();
  }

}

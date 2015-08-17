package org.ankhzet.ergo.classfactory.builder;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
public class ClassBuilder<Type> implements Builder<Class<? extends Type>, Type> {

  ReentrantLock lock = new ReentrantLock();

  Class<? extends Type> classRef;

  public ClassBuilder() {
  }

  public ClassBuilder(Class<? extends Type> classRef) {
    this.classRef = classRef;
  }

  /**
   *
   * @param c Class to be instantiated
   * @param args Arguments to pass to constructor
   * @return instantiated object
   * @throws Exception if has no accesible constructors, has more than 1
   * constructor or can't resolve it's dependencies.
   */
  @Override
  synchronized public Type build(Class<? extends Type> c, Object... args) throws Exception {
    if (!lock.tryLock())
      return null;

    try {
//      if (lock.getHoldCount() > 1)
//        return null;

      if (classRef != null)
        c = classRef;

      Constructor<? extends Type> constructor;
      try {
        constructor = c.getConstructor(types(args));
      } catch (NoSuchMethodException | SecurityException ex) {
        throw new FailedFactoryProductException(c, new Exception("Must have default constructor"));
      }

      Type instance = constructor.newInstance(args);

      return instance;
    } finally {
      lock.unlock();
    }
  }

  Class<?>[] types(Object[] args) {
    ArrayList<Class<?>> list = new ArrayList<>();
    for (Object arg : args)
      if (arg != null)
        list.add(arg.getClass());
      else
        list.add(Object.class);
    return list.toArray(new Class<?>[]{});
  }

}

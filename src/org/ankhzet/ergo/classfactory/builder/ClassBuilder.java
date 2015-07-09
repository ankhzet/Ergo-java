package org.ankhzet.ergo.classfactory.builder;

import java.lang.reflect.Constructor;
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

  /**
   *
   * @param c Class to be instantiated
   * @return instantiated object
   * @throws Exception if has no accesible constructors, has more than 1
   * constructor or can't resolve it's dependencies.
   */
  @Override
  public Type build(Class<? extends Type> c) throws Exception {
   Constructor<?> constructor = c.getConstructor();
    if ((constructor == null) || (constructor.getParameterCount() > 0))
      throw new FailedFactoryProductException(c, new Exception("Must have default constructor"));

    Type instance = (Type) constructor.newInstance();

    return instance;
  }

}

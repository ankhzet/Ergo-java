package org.ankhzet.ergo.classfactory;

import java.util.HashMap;
import org.ankhzet.ergo.classfactory.exceptions.FactoryException;
import org.ankhzet.ergo.classfactory.exceptions.UnknownFactoryProductException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoC {

  static IoC ioc = new IoC();

  public static <P> P get(Class<? extends P> identifier) {
    try {
      ClassFactory<P> factory = ioc.factory(identifier);

      if (factory == null)
        return null;

      return factory.get(identifier);
    } catch (FactoryException ex) {
      ex.printStackTrace();
    }

    return null;
  }

  public static <P> P make(Class<? extends P> identifier, Object... args) throws FactoryException {
    ClassFactory<P> factory = ioc.factory(identifier);

    return factory.make(identifier, args);
  }

  public static <P> P resolve(Class<? extends P> identifier, Object... args) throws FactoryException {
    ClassFactory<P> factory = ioc.factory(identifier);

    return factory.resolve(identifier, args);
  }

  @SuppressWarnings("unchecked")
  public static <P> ClassFactory<P> registerFactory(ClassFactory<P> factory) {
    for (Class<? extends P> identifier : factory.produces())
      ioc.mapping.put(identifier, factory);

    return factory;
  }

  HashMap<Class<?>, ClassFactory<?>> mapping = new HashMap<>();

  @SuppressWarnings("unchecked")
  <P> ClassFactory<P> factory(Class<? extends P> identifier) throws UnknownFactoryProductException {
    ClassFactory<P> f = (ClassFactory<P>) mapping.get(identifier);
    if (f != null)
      return f;

    for (Class<?> c : mapping.keySet())
      if (identifier.isAssignableFrom(c))
        return (ClassFactory<P>) mapping.get(c);

    throw new UnknownFactoryProductException(identifier);
  }

}

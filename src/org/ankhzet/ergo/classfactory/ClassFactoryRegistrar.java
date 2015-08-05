package org.ankhzet.ergo.classfactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <P>
 */
public class ClassFactoryRegistrar<P> extends FactoryRegistrar<ClassFactory<P>> {

  public ClassFactoryRegistrar(ClassFactory<P> identifier) {
    super(identifier);
  }

  @Override
  @SuppressWarnings("unchecked")
  public ClassFactory<P> getInstance(Object identifier) {
    if (identifier instanceof Class)
      try {
        Class<ClassFactory<P>> c = (Class<ClassFactory<P>>) identifier;
        return c.newInstance();
      } catch (InstantiationException | IllegalAccessException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    else
      return (ClassFactory<P>) identifier;

    return null;
  }

  @Override
  public void register(Object factoryIdentifier, ClassFactory<P> factory) {
    LOG.log(Level.FINE, "Registering class factory {0}..\n", factoryIdentifier);
    IoC.registerFactory(factory);
  }

  private static final Logger LOG = Logger.getLogger(ClassFactoryRegistrar.class.getName());

}

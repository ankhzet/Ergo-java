package org.ankhzet.ergo.classfactory;

import org.ankhzet.ergo.classfactory.builder.Builder;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Produces>
 */
public class SingleClassFactoryRegistrar<Produces> extends ClassFactoryRegistrar<Produces> {

  public SingleClassFactoryRegistrar(Class<Produces> c, Builder<Class<? extends Produces>, Produces> builder) {
    super(new SingleClassFactory<>(c, builder));
  }

  public SingleClassFactoryRegistrar(Class<Produces> c) {
    super(new SingleClassFactory<>(c));
  }

}

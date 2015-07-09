package org.ankhzet.ergo.classfactory;

import org.ankhzet.ergo.classfactory.builder.Builder;

class CommonClassFactory<Produces> extends ClassFactory<Produces> {

  public CommonClassFactory(final Class<Produces> c, Builder<Produces> builder) {
    register(c, builder);
  }

}


/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class CommonFactoryRegistrar<Produces> extends FactoryRegistrar {

  public CommonFactoryRegistrar(Class<Produces> c, Builder<Produces> builder) {
    super(new CommonClassFactory(c, builder));
  }
}


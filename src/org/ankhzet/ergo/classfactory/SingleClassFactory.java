package org.ankhzet.ergo.classfactory;

import org.ankhzet.ergo.classfactory.builder.Builder;
import org.ankhzet.ergo.classfactory.builder.ClassBuilder;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Produces>
 */
public class SingleClassFactory<Produces> extends ClassFactory<Produces> {

  SingleClassFactory(Class<Produces> c, Builder<Class<? extends Produces>, Produces> builder) {
    register(c, builder);
  }

  SingleClassFactory(Class<Produces> c) {
    register(c, new ClassBuilder<>(c));
  }

}

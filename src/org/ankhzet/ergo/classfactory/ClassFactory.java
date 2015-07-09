
package org.ankhzet.ergo.ClassFactory;

import org.ankhzet.ergo.ClassFactory.Builder.Builder;
import java.util.HashMap;

abstract class AbstractClassFactory<ProducesType> implements AbstractFactory<Class, ProducesType> {

}

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ClassFactory<ProducesType> extends AbstractClassFactory<ProducesType> {

  HashMap<Class, ProducesType> container = new HashMap<>();
  HashMap<Class, Builder<ProducesType>> builders = new HashMap<>();

  @Override
  public ClassSet produces() {
    return (ClassSet)builders.keySet();
  }

  @Override
  public ProducesType get(Class identifier) throws UnknownFactoryProductException {
    synchronized(this) {
      ProducesType instance = (ProducesType) container.get(identifier);
      if (instance == null)
        container.put(identifier, instance = make(identifier));

      return instance;
    }
  }

  @Override
  public ProducesType make(Class identifier) throws UnknownFactoryProductException {
    synchronized(this) {
      Builder<ProducesType> builder = builders.get(identifier);
      if (builder == null)
        throw new UnknownFactoryProductException(identifier);

      ProducesType instance = null;
      try {
        instance = builder.call();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      return instance;
    }
  }

  @Override
  public Builder register(Class identifier, Builder<ProducesType> builder) {
    synchronized(this) {
      Builder<ProducesType> old = builders.get(identifier);
      builders.put(identifier, builder);
      return old;
    }
  }

}

package org.ankhzet.ergo.classfactory;

import java.util.HashMap;
import org.ankhzet.ergo.classfactory.builder.Builder;
import org.ankhzet.ergo.classfactory.builder.ClassBuilder;
import org.ankhzet.ergo.classfactory.exceptions.UnknownFactoryProductException;

public class ClassFactory<P> extends Factory<Class<? extends P>, P> {

  @Override
  public Builder<Class<? extends P>, P> register(Class<? extends P> identifier) {
    return register(identifier, new ClassBuilder<>(identifier));
  }

  @Override
  <R> R pick(HashMap<Class<? extends P>, R> map, Class<? extends P> id) throws UnknownFactoryProductException {
    R picked = map.get(id);

    if (picked != null)
      return picked;

    for (Class<? extends P> c : map.keySet())
      if (id.isAssignableFrom(c))
        return map.get(c);

    throw new UnknownFactoryProductException(id);
  }

  public static <P> FactoryRegistrar<ClassFactory<P>> registerClass(Class<P> c) {
    return new SingleClassFactoryRegistrar<>(c);
  }

  public static <P> FactoryRegistrar<ClassFactory<P>> registerClass(Class<P> c, Builder<Class<? extends P>, P> builder) {
    return new SingleClassFactoryRegistrar<>(c, builder);
  }

}

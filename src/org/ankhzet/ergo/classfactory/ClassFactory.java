package org.ankhzet.ergo.classfactory;

import java.util.HashMap;
import org.ankhzet.ergo.classfactory.exceptions.UnknownFactoryProductException;

public class ClassFactory<P> extends Factory<Class<? extends P>, P> {

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

}

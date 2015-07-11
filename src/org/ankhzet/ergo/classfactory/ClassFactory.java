
package org.ankhzet.ergo.classfactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.ankhzet.ergo.classfactory.builder.Builder;
import org.ankhzet.ergo.classfactory.builder.ClassBuilder;
import org.ankhzet.ergo.classfactory.exceptions.*;

abstract class AbstractClassFactory<ProducesType> implements AbstractFactory<Class, ProducesType> {

}

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <ProducesType> Class, produced by factory
 */
public class ClassFactory<ProducesType> extends AbstractClassFactory<ProducesType> {

  static final String injectorPrefix = "di";

  HashMap<Class, ProducesType> container = new HashMap<>();
  HashMap<Class, Builder<ProducesType>> builders = new HashMap<>();

  @Override
  public Set<Class> produces() {
    return builders.keySet();
  }

  @Override
  public ProducesType get(Class identifier) throws FactoryException {
    synchronized (this) {
      ProducesType instance = (ProducesType) container.get(identifier);
      if (instance == null) {
        container.put(identifier, instance = make(identifier));

        injectDependencies(instance);
      }

      return instance;
    }
  }

  @Override
  public ProducesType make(Class identifier) throws FactoryException {
    synchronized (this) {
      Builder<ProducesType> builder = builders.get(identifier);
      if (builder == null)
        throw new UnknownFactoryProductException(identifier);

      ProducesType instance = null;
      try {
        instance = (ProducesType) builder.build(identifier);
      } catch (Exception ex) {
        boolean wrap = (!(ex instanceof FactoryException));
        throw wrap ? new FailedFactoryProductException(identifier, ex) : (FactoryException) ex;
      }

      return instance;
    }
  }

  @Override
  public Builder register(Class identifier, Builder<ProducesType> builder) {
    synchronized (this) {
      Builder<ProducesType> old = builders.get(identifier);
      builders.put(identifier, builder);
      return old;
    }
  }

  @Override
  public Builder register(Class identifier) {
    return register(identifier, new ClassBuilder<>());
  }

  void injectDependencies(Object instance) throws FailedFactoryProductException {
    Class c = instance.getClass();

    HashMap<Class, Method> injectors = getInjectPoints(c);
    if (injectors.size() > 0)
      injectors.forEach((classToInject, injector) -> {
        Object parameter = buildParam(classToInject);
        if (parameter == null)
          System.err.printf("Problems with instantiation %s in %s", classToInject.getName(), c.getName());

        try {
          injector.invoke(instance, parameter);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
          throw new RuntimeException(new FailedFactoryProductException(c, ex));
        }
      });
  }

  HashMap<Class, Method> getInjectPoints(Class c) {
    Method[] allMethods = c.getMethods();
    HashMap<Class, Method> injectors = new HashMap<>(allMethods.length);
    for (Method m : allMethods) {
      String methodName = m.getName();
      if (!methodName.startsWith(injectorPrefix))
        continue;

      Class returnType = m.getReturnType();
      if (returnType.isPrimitive() || injectors.get(returnType) != null)
        continue;

      String returns = returnType.getSimpleName();
      if (methodName.equals(injectorPrefix + returns))
        injectors.put(returnType, m);
    }

    return injectors;
  }

  protected Object buildParam(Class c) {
    return IoC.get(c);
  }

}

package org.ankhzet.ergo.classfactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.ankhzet.ergo.classfactory.annotations.DependenciesInjected;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.classfactory.builder.Builder;
import org.ankhzet.ergo.classfactory.builder.ClassBuilder;
import org.ankhzet.ergo.classfactory.exceptions.FactoryException;
import org.ankhzet.ergo.classfactory.exceptions.FailedFactoryProductException;
import org.ankhzet.ergo.classfactory.exceptions.UnknownFactoryProductException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <T> Type of producers identifiers
 * @param <P> Type of objects, produced by factory
 */
public class Factory<T, P> implements AbstractFactory<T, P> {

  HashMap<T, P> container = new HashMap<>();
  HashMap<T, Builder<T, P>> builders = new HashMap<>();

  @Override
  public Set<T> produces() {
    return builders.keySet();
  }

  @Override
  public P get(T identifier) throws FactoryException {
    synchronized (this) {
      P instance = null;
      try {
        instance = pick(container, identifier);
      } catch (UnknownFactoryProductException e) {
      }

      if (instance == null) {
        container.put(identifier, instance = make(identifier));
        injectDependencies(instance);
      }

      return instance;
    }
  }

  @Override
  public P make(T identifier, Object... args) throws FactoryException {
    synchronized (this) {
      Builder<T, P> builder = pick(builders, identifier);

      try {
        return builder.build(identifier, args);
      } catch (Exception ex) {
        boolean wrap = (!(ex instanceof FactoryException));
        throw wrap ? new FailedFactoryProductException(identifier, ex) : (FactoryException) ex;
      }
    }
  }

  @Override
  public P resolve(T identifier, Object... args) throws FactoryException {
    synchronized (this) {
      P instance = make(identifier, args);
      if (instance != null)
        injectDependencies(instance);
      return instance;
    }
  }
  
  

  <R> R pick(HashMap<T, R> map, T id) throws UnknownFactoryProductException {
    throw new UnknownFactoryProductException(id);
  }

  @Override
  public Builder<T, P> register(T identifier, Builder<T, P> builder) {
    synchronized (this) {
      Builder<T, P> old = builders.get(identifier);
      builders.put(identifier, builder);
      return old;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Builder<T, P> register(T identifier) {
    throw new RuntimeException("No default builder");
  }

  void injectDependencies(Object instance) throws FactoryException {
    Class<?> c = instance.getClass();
    DependencyNode dependencies = collectDependencies(instance, c);

    while (dependencies != null) {
      dependencies.inject(instance);
      dependencies = dependencies.next;
    }
  }

  private DependencyNode collectDependencies(Object instance, Class<?> c) {

    Method method = Stream.of(c.getDeclaredMethods())
      .filter(m -> m.isAnnotationPresent(DependenciesInjected.class
        ))
      .findFirst()
      .orElse(null);

    FieldsList fields = new FieldsList(Stream.of(c.getDeclaredFields())
      .filter(field -> field.isAnnotationPresent(DependencyInjection.class))
      .collect(Collectors.toList()));

    DependencyNode current = new DependencyNode(fields, method, c);

    boolean suppress = false;
    boolean reversed = false;

    if (method != null) {
      method.setAccessible(true);
      DependenciesInjected annotation = method.getAnnotation(DependenciesInjected.class);
      suppress = annotation.suppressInherited();
      reversed = annotation.beforeInherited();
    }

    DependencyNode superclass = null;
    if (!suppress) {
      c = c.getSuperclass();
      if (c != null && c != Object.class)
        superclass = collectDependencies(instance, c);
    }

    if (superclass != null)
      if (!reversed) {
        superclass.append(current);
        current = superclass;
      } else
        current.append(superclass);

    return current.empty() ? current.next : current;
  }

  private class FieldsList extends ArrayList<Field> {

    FieldsList(Collection<? extends Field> c) {
      super(c);
    }

  };

  private class DependencyNode {

    FieldsList fields;
    Method finisher;
    Class<?> forClass;
    DependencyNode next;

    DependencyNode(FieldsList fields, Method finisher, Class<?> forClass) {
      this.fields = fields;
      this.finisher = finisher;
      this.forClass = forClass;
    }

    void append(DependencyNode node) {
      if (this.next == null)
        this.next = node;
      else
        this.next.append(node);
    }

    boolean empty() {
      return (finisher == null) && (fields.size() == 0);
    }

    void inject(Object instance) throws FactoryException {
      try {
        if (!fields.isEmpty())
          makeDependencies(instance);

        if (finisher != null)
          invokeFinisher(instance);
      } catch (Exception ex) {
        if (!(ex instanceof FactoryException))
          throw new FailedFactoryProductException(forClass, ex);
        throw (FactoryException) ex;
      }
    }

    void makeDependencies(Object instance) throws Exception {
      for (Field field : fields) {
//        System.out.printf("Dependency [(%s)%s] -> (%s)%s\n", 
//                forClass.getSimpleName(), 
//                instance.getClass().getSimpleName(), 
//                field.getType().getSimpleName(),
//                field.getName());
        DependencyInjection di = field.getAnnotation(DependencyInjection.class);

        Object param = makeDependency(field.getType(), di.instantiate());

        field.setAccessible(true);
        field.set(instance, param);
      }
    }

    void invokeFinisher(Object instance) throws Exception {
      finisher.setAccessible(true);
      finisher.invoke(instance);
    }

    Object makeDependency(Class<?> c, boolean instantiate) throws FactoryException {
      return instantiate ? IoC.make(c) : IoC.get(c);
    }

  }

}

package org.ankhzet.ergo.db.query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Grammar {

  static final int//
    T_STRING = 1,
    T_INTEGER = 2,
    T_BOOLEAN = 3,
    T_ARRAY = 4,
    T_LIST = 5;

  protected HashMap<String, Integer> components = new HashMap<>();

  public HashMap<String, String> compileComponents(Builder query) {
    HashMap<String, String> sqls = new HashMap<>();

    Field f;
    for (String component : components.keySet())
      if ((f = hasProperty(query, component)) != null)
        sqls.put(component, compileComponent(query, f));

    return sqls;
  }

  protected String compileComponent(Builder query, Field component) {
    String compiler = "compile" + Strings.toTitleCase(component.getName());
    try {
      Method m = getClass().getDeclaredMethod(compiler, new Class[]{Builder.class});
      return (String) m.invoke(this, query);
    } catch (NoSuchMethodException |
             SecurityException |
             IllegalAccessException |
             IllegalArgumentException |
             InvocationTargetException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public <T> T valueOf(Object o, String property, T def) {
    T value = (T) valueOf(o, hasProperty(o, property));
    return value != null ? value : def;
  }

  protected Object valueOf(Object o, Field f) {
    try {
      return f.get(o);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
    }
    return null;
  }

  protected Field hasProperty(Object o, String property) {
    try {
      Class c = o.getClass();

      Field f = null;
      do {
        try {
          f = c.getDeclaredField(property);
          if (f != null)
            break;
        } catch (NoSuchFieldException e) {
        }
      } while ((c = c.getSuperclass()) != null);

      if (f == null)
        return null;

      Object value = f.get(o);
      if (value == null)
        return null;

      Integer type = components.get(property);
      if (type == null)
        return f;

      switch (type) {
      case T_LIST:
        if (((List) value).isEmpty())
          return null;
        break;

      case T_ARRAY:
        if (((Object[]) value).length == 0)
          return null;
        break;

      case T_BOOLEAN:
        if (!(Boolean) value)
          return null;
        break;

      case T_INTEGER:
        if ((Integer) value == 0)
          return null;
        break;
      }

      return f;
    } catch (SecurityException | IllegalAccessException ex) {
      ex.printStackTrace();
    }

    return null;
  }

}

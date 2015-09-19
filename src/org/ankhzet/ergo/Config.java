package org.ankhzet.ergo;

import java.util.HashMap;
import org.ankhzet.ergo.files.Parser;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Config extends ConfigNode {

  public static String cfgFilePath(String cfgName) {
    return App.appDir("config/" + cfgName + ".cfg");
  }

  public Config(String src) {
    readFromParser(new Parser(cfgFilePath(src)));
  }

  public Config(Parser p) {
    readFromParser(p);
  }

  final void readFromParser(Parser p) {
    try {
      p.checkAndNext("{");
      
      do {
        String node = p.Token;
        if (node.isEmpty() || node.equalsIgnoreCase("}"))
          break;
        
        p.next();
        if (p.isToken("=")) {
          put(node, decode(p.Token));
          p.nextAndCheck(";");
          p.next();
        } else
          put(node, new Config(p));
        
      } while(true);
      
      p.checkAndNext("}");
    } catch (Throwable ex) {
      throw new RuntimeException("Failed to parse config: " + ex.getLocalizedMessage(), ex);
    }
  }
  
  Object decode(String value) {
    if (value.isEmpty())
      return value;
    
    if (value.matches("\\s"))
      return value;
    
    if (value.matches("^\\d*[\\.,]\\d*$"))
      return Double.valueOf(value);
    
    if (value.matches("^\\d*$"))
      return Long.valueOf(value);
    
    if (value.matches("^(0[xX]|#)\\p{XDigit}+$"))
      return Long.valueOf(value, 16);
    
    if (value.matches("^(?i)(true|false)$"))
      return Boolean.valueOf(value);
    
    return value;
  }
  
}

class ConfigNode extends HashMap<String, Object> {

  public <T> T get(String key, T def) {
    Object value = get(key);
    return (value != null) ? (T) value : def;
  }
  
  public Object get(String key) {
    Strings path = Strings.explode(key, "\\.");
    String childKey = path.shift();

    Object child = super.get(childKey);

    if ((child == null) || (path.size() == 0))
      return child;

    if (child instanceof ConfigNode)
      return ((ConfigNode) child).get(path.join("."));

    throw new RuntimeException(String.format("%s entry should be instance of %s, but %s fould", key, ConfigNode.class.getName(), child.getClass().getName()));
  }

  public void set(String key, Object value) {
    Strings path = Strings.explode(key, "\\.");
    String childKey = path.shift();

    if (path.size() == 0)
      put(childKey, value);
    else {
      ConfigNode child = (ConfigNode)super.get(childKey);

      if (child == null)
        put(childKey, child = new ConfigNode());
      else
        if (!(child instanceof ConfigNode))
          throw new RuntimeException(String.format("%s entry should be instance of %s, but %s fould", key, ConfigNode.class.getName(), child.getClass().getName()));

      child.set(path.join("."), value);
    }
  }

}

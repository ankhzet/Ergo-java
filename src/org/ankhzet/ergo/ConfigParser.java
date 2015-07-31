
package org.ankhzet.ergo;

import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.files.Parser;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConfigParser extends Parser {
  static Config cfg;
  
  static Config getConfig() {
    if (cfg == null)
      cfg = IoC.get(Config.class);

    return cfg;
  }
  
  public ConfigParser(String src) {
    super(getConfig().cfgFilePath(src));
  }

}

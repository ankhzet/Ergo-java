package org.ankhzet.ergo;

import org.ankhzet.ergo.files.Parser;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConfigParser extends Parser {
  
  public ConfigParser(String src) {
    super(Config.cfgFilePath(src));
  }

}

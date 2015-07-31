
package org.ankhzet.ergo;

import java.io.File;
import java.nio.file.Path;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Config {
  public static final String APP_NAME = "ergo";
  
  public String appName() {
    return APP_NAME;
  }

  public String appDir() {
    return String.format("%s/.%s", System.getProperty("user.home"), appName());
  }
  
  public String appDir(String relative) {
    File f = new File(String.format("%s/.%s", System.getProperty("user.home"), appName()));
    Path r = f.toPath().resolve(relative);
    return r.toString();
  }
  
  public String cfgFilePath(String cfgName) {
    return appDir("config/" + cfgName + ".cfg");
  }
}

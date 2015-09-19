package org.ankhzet.ergo;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.security.CodeSource;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class App {

  public static final String APP_NAME = "ergo";

  public static String appName() {
    return APP_NAME;
  }

  public static String resolveDir(String dir) {
    if ((new File(dir)).getName().equals(dir))
      return appDir(dir);

    if (dir.contains("%app")) {
      dir = dir.replace("%app", appContainingFolder());
      File f = new File(dir);
      dir = f.toPath().toString();
    }
    return dir;
  }

  public static String appDir() {
    return String.format("%s/.%s", System.getProperty("user.home"), appName());
  }

  public static String appDir(String relative) {
    File f = new File(appDir());
    Path r = f.toPath().resolve(relative);
    return r.toString();
  }

  public static String appContainingFolder() {
    try {
      CodeSource codeSource = App.class.getProtectionDomain().getCodeSource();

      File jarFile;

      if (codeSource.getLocation() != null)
        jarFile = new File(codeSource.getLocation().toURI());
      else {
        String path = App.class.getResource(App.class.getSimpleName() + ".class").getPath();
        String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
        jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
        jarFile = new File(jarFilePath);
      }
      return jarFile.getParentFile().getAbsolutePath();
    } catch (URISyntaxException | UnsupportedEncodingException ex) {
      throw new RuntimeException("Failed to examine app path", ex);
    }
  }

}

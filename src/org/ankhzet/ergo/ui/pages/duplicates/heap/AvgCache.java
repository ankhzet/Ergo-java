package org.ankhzet.ergo.ui.pages.duplicates.heap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ankhzet.ergo.utils.FColor;
import org.ankhzet.ergo.utils.ImgUtil;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AvgCache extends HashMap<String, Integer> {

  Path cachePath;

  public void loadCache(Path cachePath) {
    this.cachePath = cachePath;
    File[] avMaches = cachePath.toFile().listFiles((File file) -> {
      return file.getName().endsWith(".col");
    });

    if (avMaches != null) {
      Pattern p = Pattern.compile("(.*)\\.(\\d+)\\.col");

      for (File f : avMaches) {
        Matcher m = p.matcher(f.getName());
        if (m.find())
          try {
            Integer average = Integer.decode(m.group(2));
            put(m.group(1), average);
          } catch (NumberFormatException e) {
          }
      }
    }
  }

  public FColor imageAverage(String page, BufferedImage image) throws IOException {
    Integer avg;

    synchronized (this) {
      avg = get(page = Strings.md5(page));
    }

    if (avg != null)
      return new FColor(avg);
    else {
      FColor average = ImgUtil.imageAverage(image);
      cacheAvg(page, average);
      return average;
    }
  }

  void cacheAvg(String page, FColor average) throws IOException {
    Path path = cachePath.resolve(String.format("%s.%d.col", page, average.getRGB()));
    File col = path.toFile();
    col.getParentFile().mkdirs();
    col.createNewFile();
  }

}

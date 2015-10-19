package org.ankhzet.ergo.ui.pages.duplicates.heap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.ankhzet.ergo.ui.LoaderProgressListener;
import org.ankhzet.ergo.utils.FColor;
import org.ankhzet.ergo.utils.ImgUtil;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ImageHeap extends AvgCache {

  int cacheHeight;

  HashMap<FColor, ArrayList<BufferedImage>> coloredImages = new HashMap<>();
  HashMap<BufferedImage, FColor> imageColor = new HashMap<>();
  HashMap<BufferedImage, String> imageFile = new HashMap<>();
  ArrayList<BufferedImage> duplicates = new ArrayList<>();
  ArrayList<FColor> colorsCache = null;

  Path searchPath;
  Path duplicatesPath;
  Path thumbPath;

  LoaderProgressListener progressListener;

  ProcessThreadsPool processors = new ProcessThreadsPool();

  public ImageHeap(int cacheHeight, Path searchPath) {
    this.searchPath = searchPath;
    this.cacheHeight = cacheHeight;

    Path base = searchPath.resolve(".duplicates");
    duplicatesPath = base.resolve("trash");
    thumbPath = base.resolve("thumbnails");
    loadCache(base.resolve("averages"));
    duplicatesPath.toFile().mkdirs();
    thumbPath.toFile().mkdirs();
  }

  public void setProgressListener(LoaderProgressListener l) {
    progressListener = l;
  }

  public void fetchDuplicates(int threads) {
    coloredImages.clear();
    duplicates.clear();

    Strings pages = new Strings();

    try {
      Files.walkFileTree(searchPath, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          if (!(file.startsWith(cachePath) || file.startsWith(thumbPath) || file.startsWith(duplicatesPath)))
            if (file.getFileName().toString().matches(".*(?i)\\.(png|jpe?g|bmp|gif)$"))
              pages.add(file.toString());

          return FileVisitResult.CONTINUE;
        }

      });
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    processors.init(this, pages, threads, progressListener);
    processors.start();
  }

  synchronized public String imagePath(BufferedImage image) {
    return imageFile.get(image);
  }

  public void moveDuplicates() {
    duplicates.stream()
      .map((img) -> new File(imageFile.get(img)))
      .forEach((f) -> {
        File to = duplicatesPath.resolve(f.getName()).toFile();
        if (to.exists()) {
          String name = to.getName();
          int i = 1;
          while (to.exists())
            to = to.toPath().resolveSibling(String.format("%d-%s", i++, name)).toFile();
        }

        f.renameTo(to);
        System.out.printf("Renamed:\n  \"%s\" -> \"%s\"\n", f.getPath(), to.getPath());
      });
  }

  public boolean hasDuplicates() {
    return duplicates.size() > 0;
  }

  public boolean isDuplicate(BufferedImage image) {
    return duplicates.contains(image);
  }

  public boolean toggleDuplicate(BufferedImage image) {
    boolean has = isDuplicate(image);

    if (has)
      duplicates.remove(image);
    else
      duplicates.add(image);

    return has;
  }

  synchronized public ArrayList<BufferedImage> getColor(FColor color) {
    ArrayList<BufferedImage> images = coloredImages.get(color);
    if (images != null) {
      ArrayList<BufferedImage> copy = new ArrayList<>(images);
      try {
        copy.sort((i1, i2) -> {
          FColor c1 = imageColor.get(i1);
          FColor c2 = imageColor.get(i2);
          return c1.compare(c2);
        });
      } catch (Exception e) {
        System.err.println(e.getLocalizedMessage());
      }
      return copy;
    }
    return null;
  }

  synchronized public ArrayList<FColor> colors() {
    if (colorsCache == null) {
      colorsCache = new ArrayList<>(coloredImages.keySet());

      HashMap<FColor, Integer> dupMap = new HashMap<>();

      colorsCache.sort((c1, c2) -> {
        Integer d1 = dupMap.get(c1);
        Integer d2 = dupMap.get(c2);
        if (d1 == null) {
          ArrayList<BufferedImage> images = getColor(c1);
          dupMap.put(c1, d1 = images.size());
        }
        if (d2 == null) {
          ArrayList<BufferedImage> images = getColor(c2);
          dupMap.put(c2, d2 = images.size());
        }
        int delta = Integer.signum(d2 - d1);
        if (delta == 0)
          delta = Integer.signum(c1.brightness() - c2.brightness());
        return delta;
      });
    }

    return colorsCache;
  }

  synchronized void putImage(String page, FColor color, BufferedImage image) {
    FColor group = near(color);

    imageColor.put(image, color);

    ArrayList<BufferedImage> array = coloredImages.get(group);
    if (array == null) {
      array = new ArrayList<>();
      coloredImages.put(group, array);
    }
    colorsCache = null;

    array.add(image);

    imageFile.put(image, page);
  }

  synchronized FColor near(FColor color) {
    for (FColor c : coloredImages.keySet())
      if (c.equals(color))
        return c;

    return color;
  }

  synchronized void putColor(FColor color, ArrayList<BufferedImage> images) {
    coloredImages.put(color, images);
    colorsCache = null;
  }

  BufferedImage thumbnail(String page) throws IOException {
    return ImgUtil.thumbnail(page, thumbPath.toString(), img -> {
      int height = img.getHeight();
      if (height <= cacheHeight)
        return img;

      return ImgUtil.scaled(img, cacheHeight / (float) height);
    });
  }

}

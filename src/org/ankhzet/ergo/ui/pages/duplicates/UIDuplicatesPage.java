package org.ankhzet.ergo.ui.pages.duplicates;

import org.ankhzet.ergo.utils.FColor;
import org.ankhzet.ergo.utils.ImgUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.ui.pages.UIPage;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIDuplicatesPage extends UIPage {

  static final int cacheHeight = 100,
    thumbHeight = 50,
    thumbSpace = 5;

  int mx, my;
  BufferedImage overI = null;

  HashMap<FColor, ArrayList<BufferedImage>> coloredImages = new HashMap<>();
  HashMap<BufferedImage, FColor> imageColor = new HashMap<>();
  HashMap<BufferedImage, String> imageFile = new HashMap<>();
  HashMap<String, Integer> avgs = new HashMap<>();

  ArrayList<BufferedImage> duplicates = new ArrayList<>();

  Chapter duplicatesIn;

  Chapter searchPath() {
    return duplicatesIn;
  }

  @Override
  public void navigateIn(Object... params) {
    if (params.length < 1)
      throw new RuntimeException("Path not specified");

    ui.intensiveRepaint(true);

    duplicatesIn = new Chapter((String) params[0]);

    File cols = duplicatesIn.toPath().resolve(".col").toFile();
    File[] avMaches = cols.listFiles((File file) -> {
      return file.getName().endsWith(".col");
    });

    if (avMaches != null)
      for (File f : avMaches) {
        String n = f.getName().replace(".col", "");
        Strings parts = Strings.explode(n, "\\.");
        n = parts.pop();
        try {
          Integer avg = Integer.decode(n);
          avgs.put(parts.join("."), avg);
        } catch (NumberFormatException e) {
        }
      }

    fetchDuplicates();

    loadHUD();
  }

  void loadHUD() {
    hud.putActionAtLeft("Move duplicates", registerAction("move-dups", action -> {
      moveDuplicates();
    }).enabledAs(action -> {
      return duplicates.size() > 0;
    }));
  }

  void fetchDuplicates() {
    coloredImages.clear();

    Chapter c = searchPath();
    Strings pages = c.fetchPages();
    Path cols = c.toPath().resolve(".col");

    (new Thread(() -> {
      ui.onProgress(UILogic.STATE_CACHING, 0, pages.size());
      pages.stream().forEach((page) -> {
        try {
          File f = new File(page);
          BufferedImage image = ImgUtil.thumbnail(page, "thumb", img -> {
            return ImgUtil.scaled(img, cacheHeight / (float) img.getHeight());
          });

          FColor average;
          Integer avg = avgs.get(f.getName());
          if (avg != null)
            average = new FColor(avg);
          else {
            average = ImgUtil.imageAverage(image);
            Path path = cols.resolve(String.format("%s.%d.col", f.getName(), average.getRGB()));
            File col = path.toFile();
            col.getParentFile().mkdirs();
            col.createNewFile();
          }

          putImage(average, image);
          imageFile.put(image, page);

          Thread.sleep(10);
        } catch (IOException | InterruptedException ex) {
          ex.printStackTrace();
        }
        ui.onProgress(UILogic.STATE_CACHING, pages.indexOf(page), pages.size());
        System.gc();
      });
      ui.progressDone();
    })).start();
  }

  void moveDuplicates() {
    Path to = searchPath().toPath().resolve("dups");
    to.toFile().mkdirs();

    for (BufferedImage img : duplicates) {
      String path = imageFile.get(img);
      File f = new File(path);

      f.renameTo(to.resolve(f.getName()).toFile());
    }

    fetchDuplicates();
  }

  FColor near(FColor color) {
    synchronized (this) {
      for (FColor c : coloredImages.keySet())
        if (c.equals(color))
          return c;
    }

    return color;
  }

  void putImage(FColor color, BufferedImage i) {
    FColor group = near(color);

    synchronized (this) {
      imageColor.put(i, color);

      ArrayList<BufferedImage> array = coloredImages.get(group);
      if (array == null) {
        array = new ArrayList<>();
        coloredImages.put(group, array);
      }

      array.add(i);
    }
  }

  void putColor(FColor color, ArrayList<BufferedImage> images) {
    synchronized (this) {
      coloredImages.put(color, images);
    }
  }

  ArrayList<BufferedImage> getColor(FColor color) {
    synchronized (this) {
      ArrayList<BufferedImage> images = coloredImages.get(color);
      if (images != null) {
        ArrayList<BufferedImage> copy = new ArrayList<>(images.size());
        images.stream().forEach((i) -> {
          copy.add(i);
        });
        copy.sort((i1, i2) -> {
          FColor c1 = imageColor.get(i1);
          FColor c2 = imageColor.get(i2);
          return c1.compare(c2);
        });
        return copy;
      }
      return null;
    }
  }

  FColor[] colors() {
    synchronized (this) {
      return coloredImages.keySet().toArray(new FColor[]{});
    }
  }

  int imageWidth(BufferedImage image) {
    float s = image.getWidth() / (float) image.getHeight();
    return (int) (s * thumbHeight);
  }

  boolean enoughtSpace(ArrayList<BufferedImage> images, int w) {
    int sum = 0;
    sum = images.stream().map((image) -> imageWidth(image)).reduce(sum, Integer::sum);

    sum += Math.max(0, images.size() - 1) * thumbSpace;
    return sum <= w;
  }

  @Override
  public void draw(Graphics2D g, int w, int h) {
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

    int offsetY = 0;
    int offsetX = 0;
    overI = null;
    Rectangle overR = null;

    FColor[] colorsA = colors();
    ArrayList<FColor> colors = new ArrayList<>(Arrays.asList(colorsA));
    colors.sort((c1, c2) -> {
      return c1.compare(c2);
    });
    for (FColor color : colors) {
      ArrayList<BufferedImage> images = getColor(color);
//      UILogic.log("Images for c %s: %d", color.toString(), images.size());
      if ((images == null) || images.isEmpty())
        continue;

      if (!enoughtSpace(images, w - offsetX - 10) && (offsetX > 0)) {
        offsetX = 0;
        offsetY += 15 + thumbHeight;
      }

      int tipX = offsetX;
      int tipW = tipX;
      int tipY = offsetY;

      boolean shouldDrop = false;
      for (BufferedImage image : images) {
        int thumbWidth = imageWidth(image);
        int newX = offsetX + 5 + thumbWidth;
        if (newX > w - 5) {
          offsetX = 0;
          offsetY += 15 + thumbHeight;
          newX = 0;
          shouldDrop = true;
        }
        tipW = Math.max(tipW, newX);

        Rectangle r = new Rectangle(offsetX + 5, offsetY + 10, thumbWidth, thumbHeight);
        if (r.contains(mx, my)) {
          overR = scaleRectangle(r, image);
          overI = image;
        } else {
        }

        drawImage(image, g, r);

        offsetX = newX;

        image.flush();
      }

      drawTip(color, g, tipX, tipY, tipW - tipX + 5, 5);
      offsetX += 10;
    }

    if (overI != null)
      drawImage(overI, g, overR);
  }

  void highlite(BufferedImage image, Graphics2D g) {
    if (duplicates.contains(image))
      g.setColor(Color.RED);
    else
      g.setColor(Color.GRAY);
  }

  Rectangle scaleRectangle(Rectangle r, BufferedImage i) {
    Rectangle scaled = (Rectangle) r.clone();
    float yscale = (cacheHeight - r.height) / 2;
    float aspect = r.width / (float) r.height;
    float xscale = yscale * aspect;

    scaled.grow((int) xscale, (int) yscale);
    return scaled;
  }

  void drawImage(BufferedImage image, Graphics2D g, Rectangle r) {
    highlite(image, g);
    g.drawImage(image, r.x, r.y, r.width, r.height, null);
    g.drawRect(r.x - 1, r.y - 1, r.width + 1, r.height + 1);
  }

  void drawTip(Color color, Graphics2D g, int x, int y, int w, int h) {
    g.setColor(color);
    g.fillRect(x, y, w, h);
  }

  @Override
  public boolean mouseEvent(MouseEvent e) {
    if (super.mouseEvent(e))
      return true;

    switch (e.getID()) {
    case MouseEvent.MOUSE_MOVED:
      mx = e.getX();
      my = e.getY();
      return true;

    case MouseEvent.MOUSE_PRESSED:
      if (overI != null)
        if (!duplicates.contains(overI))
          duplicates.add(overI);
        else
          duplicates.remove(overI);
      return true;
    }

    return false;
  }

  @Override
  public String title() {
    return "Find duplicates";
  }

}

package org.ankhzet.ergo.ui.pages.duplicates;

import org.ankhzet.ergo.utils.FColor;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import org.ankhzet.ergo.manga.chapter.Chapter;
import org.ankhzet.ergo.ui.Skin;
import org.ankhzet.ergo.ui.pages.UIPage;
import org.ankhzet.ergo.ui.pages.duplicates.heap.ImageHeap;
import org.ankhzet.ergo.utils.Utils;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UIDuplicatesPage extends UIPage {

  static final int cacheHeight = 100,
    thumbHeight = 50,
    thumbSpace = 5;

  BufferedImage overI = null;

  ImageHeap heap;

  int fetchThreads = 2;

  int scroll;
  int scrollY;

  @Override
  public void navigateIn(Object... params) {
    if (params.length < 1)
      throw new RuntimeException("Path not specified");

    ui.intensiveRepaint(true);

    ui.message("Mark duplicates with mouse click and then\nclick \"Move duplicates\" to move duplicates", 1000);

    heap = new ImageHeap(cacheHeight, new File((String) params[0]).toPath());
    heap.setProgressListener(ui);

    loadHUD();

    fetchDuplicates();
  }

  void loadHUD() {
    hud.putActionAtLeft("Move duplicates", registerAction("move-dups", action -> {
      heap.moveDuplicates();
      fetchDuplicates();
    }).enabledAs(action -> {
      return heap.hasDuplicates();
    }));
  }

  void fetchDuplicates() {
    scroll = 0;
    scrollY = 0;
    heap.fetchDuplicates(fetchThreads);
  }

  int imageWidth(BufferedImage image) {
    float s = image.getWidth() / (float) image.getHeight();
    return (int) (s * thumbHeight);
  }

  boolean enoughtSpace(ArrayList<BufferedImage> images, int w) {
    int sum = images.stream().map((image) -> imageWidth(image)).reduce(0, Integer::sum);

    sum += Math.max(0, images.size() - 1) * thumbSpace;
    return sum <= w;
  }

  @Override
  public void draw(Graphics2D g, int w, int h) {
    if (heap == null)
      return;

    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

    g.translate(0, -scroll);

    int offsetY = 0;
    int offsetX = 0;
    overI = null;
    Rectangle overR = null;

    ArrayList<FColor> colors = heap.colors();

    for (FColor color : colors) {
      ArrayList<BufferedImage> images = heap.getColor(color);
      if ((images == null) || images.isEmpty())
        continue;

      if (!enoughtSpace(images, w - offsetX - 10) && (offsetX > 0)) {
        offsetX = 0;
        offsetY += 15 + thumbHeight;
      }

      int tipX = offsetX;
      int tipW = tipX;
      int tipY = offsetY;

      for (BufferedImage image : images) {
        int thumbWidth = imageWidth(image);
        int newX = offsetX + 5 + thumbWidth;
        if (newX > w - 5) {
          offsetX = 0;
          offsetY += 15 + thumbHeight;
          newX = 0;
        }
        tipW = Math.max(tipW, newX);

        Rectangle r = new Rectangle(offsetX + 5, offsetY + 10, thumbWidth, thumbHeight);
        if (r.contains(mx, my + scroll)) {
          overR = scaleRectangle(r, image);
          overI = image;
        } else {
        }

        if (r.intersects(0, scroll, w, h)) {
          drawImage(image, g, r);
          image.flush();
        }

        offsetX = newX;
      }

      drawTip(color, g, tipX, tipY, tipW - tipX + 5, 5);
      offsetX += 10;
    }

    if (overI != null) {
      drawImage(overI, g, overR);
      Skin.drawLabel(g, heap.imagePath(overI), overR.x + overR.width / 2, overR.y + overR.height, w, h);
    }

    g.translate(0, scroll);

    scrollY = offsetY - h;
    if (scrollY > 0) {
      int scrollbarSize = 4;
      int ch = h - scrollbarSize;
      double swRatio = ch / (double) (ch + scrollY);
      int scrollHeight = (int) (ch * swRatio);
      int pos = (int) (scroll * swRatio);
      Skin.drawScrollbar(g, w - scrollbarSize - 1, pos, scrollbarSize, scrollHeight);
    }

  }

  void highlite(BufferedImage image, Graphics2D g) {
    g.setColor(heap.isDuplicate(image) ? Color.RED : Color.GRAY);
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
    if (super.mouseEvent(e)) {
      if (overI != null)
        heap.toggleDuplicate(overI);
      return true;
    }
    return false;
  }

  @Override
  public void scroll(int x, int y) {
    scroll = Utils.constraint(scroll + y, 0, scrollY);
  }

  @Override
  public String title() {
    return "Find duplicates";
  }

}

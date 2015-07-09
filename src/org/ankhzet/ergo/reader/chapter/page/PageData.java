package org.ankhzet.ergo.reader.chapter.page;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import javax.imageio.ImageIO;
import org.ankhzet.ergo.UILogic;
import org.ankhzet.ergo.reader.PageRenderOptions;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class PageData {

  String file;
  BufferedImage image, cache;
  public int pageW;
  public int pageH;
  PageLayout layout = new PageLayout(0, 0);

  private PageData(String imageFile) {
    file = imageFile;
    image = loadPage(imageFile);
    pageW = image != null ? image.getWidth() : 32;
    pageH = image != null ? image.getHeight() : 32;
  }

  public static PageData load(String imageFile) {
    return new PageData(imageFile);
  }

  final BufferedImage loadPage(String src) {
    try {

      /*Image i = Toolkit.getDefaultToolkit().createImage(src);
      int w = i.getWidth(null);
      int h = i.getHeight(null);
      BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      Graphics g = b.getGraphics();
      g.drawImage(i, w, h, null);
      return b;*/
      return ImageIO.read(new File(src));
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
  
  public PageLayout getLayout() {
    return layout;
  }
  
  public BufferedImage getImage() {
    return image;
  }

  public void calcLayout(int w, int h, PageRenderOptions ro) {
    layout.clientW = w;
    layout.clientH = h;
    layout.calcLayout(pageW, pageH, ro);
  }

  public void makeCache(PageRenderOptions options) {
    UILogic.log("caching \"%s\"", file);

    int nw = layout.newPageW;
    int nh = layout.newPageH;
    if (image == null) {
      cache = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
      return;
    }

    if (!options.originalSize)
      if (cache != null) {
        cache.flush();
        cache.getGraphics().dispose();
        cache = null;
      }

    boolean clientPortrait = layout.clientW < layout.clientH;
    boolean pagePortrait = pageW < pageH;
    boolean rotate = options.rotateToFit && (clientPortrait ^ pagePortrait);

    if (rotate) {
      AffineTransform at = new AffineTransform();
      at.scale(nh / (double) pageW, nw / (double) pageH);
      at.translate(pageH / 2d, 0);
      if (options.turnClockwise) {
        at.rotate(Math.PI / 2d);
        at.translate(0d, -pageH / 2d);
      } else {
        at.rotate(-Math.PI / 2d);
        at.translate(-pageW, -pageH / 2d);//, -pageH / 2d);
      }
//      at.scale(nw / (double) pageW, nh / (double) pageH);
      BufferedImageOp o = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
      try {
        cache = o.filter(image, null);
      } catch (Exception e) {
        UILogic.log("Image rotation failed!", 0);
      }
    } else
      if (options.originalSize)
        cache = image;
      else {
        cache = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = cache.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image, 0, 0, layout.newPageW, layout.newPageH, 0, 0, pageW, pageH, null);
        g.dispose();
      }
  }

  public void drawPage(Graphics g, int dx, int dy, int scrollX, int scrollY) {
    g.drawImage(
    cache//
    , dx + layout.renderX - scrollX//
    , dy + layout.renderY - scrollY//
    , null);
  }
}


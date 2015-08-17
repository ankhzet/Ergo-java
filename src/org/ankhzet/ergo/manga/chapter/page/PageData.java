package org.ankhzet.ergo.manga.chapter.page;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.utils.ImgUtil;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class PageData extends PageLayout {

  String file;
  BufferedImage image, cache;
  public int pageW;
  public int pageH;

  public PageData(String imageFile) {
    super(0, 0);
    file = imageFile;
  }

  public BufferedImage getImage() {
    return image;
  }

  public void draw(Graphics g, int dx, int dy) {
    g.drawImage(cache, dx + renderX, dy + renderY, null);
  }

  @Override
  public int hashCode() {
    return file.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final PageData other = (PageData) obj;
    return Objects.equals(this.file, other.file);
  }

  public boolean load() {
    try {
      image = ImageIO.read(new File(file));
      pageW = image != null ? image.getWidth() : 32;
      pageH = image != null ? image.getHeight() : 32;
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return image != null;
  }

  public boolean prepare(ReadOptions options) {
    if (!wasResized())
      return true;

    UILogic.log("caching \"%s\"", file);

    int nw = newPageW;
    int nh = newPageH;
    if (image == null) {
      cache = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
      return true;
    }

    if (!options.originalSize())
      if (cache != null) {
        cache.flush();
        cache.getGraphics().dispose();
        cache = null;
      }

    boolean clientPortrait = clientW < clientH;
    boolean pagePortrait = pageW < pageH;
    boolean rotate = options.rotateToFit() && (clientPortrait ^ pagePortrait);

    if (rotate) {
      AffineTransform at = new AffineTransform();
      at.scale(nh / (double) pageW, nw / (double) pageH);
      at.translate(pageH / 2d, 0);
      if (options.turnClockwise()) {
        at.rotate(Math.PI / 2d);
        at.translate(0d, -pageH / 2d);
      } else {
        at.rotate(-Math.PI / 2d);
        at.translate(-pageW, -pageH / 2d);
      }

      BufferedImageOp o = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
      try {
        cache = o.filter(image, null);
      } catch (Exception e) {
        UILogic.log("Image rotation failed!", 0);
        return false;
      }
    } else
      if (options.originalSize())
        cache = image;
      else {
        float scale = newPageW / (float) pageW;
        scale = 0.1f * (int) (scale * 10);
        if ((int) scale != 1)
          cache = ImgUtil.scaled(image, newPageW / (float) pageW);
        else
          cache = image;
      }

    return true;
  }

  public boolean layout(int w, int h, ReadOptions ro) {
    clientW = w;
    clientH = h;
    return calcLayout(pageW, pageH, ro);
  }

}

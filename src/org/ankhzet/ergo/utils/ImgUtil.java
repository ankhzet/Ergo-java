package org.ankhzet.ergo.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ImgUtil {

  public static FColor imageAverage(BufferedImage img) {

    int w = img.getWidth();
    int h = img.getHeight();
    int pixelCount = w * h;
    int[] pixels = new int[pixelCount];
    img.getRGB(0, 0, w, h, pixels, 0, w);

    int redBucket = 0;
    int greenBucket = 0;
    int blueBucket = 0;

    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++) {
        int color = pixels[x + y * w]; // x + y * width
        redBucket += (color >> 16) & 0xFF; // Color.red
        greenBucket += (color >> 8) & 0xFF; // Color.greed
        blueBucket += (color & 0xFF); // Color.blue
      }

    return new FColor(
      redBucket / pixelCount,
      greenBucket / pixelCount,
      blueBucket / pixelCount);

  }

  public static BufferedImage toBufferedImage(Image img) {
    if (img instanceof BufferedImage)
      return (BufferedImage) img;

    // Create a buffered image with transparency
    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    // Draw the image on to the buffered image
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(img, 0, 0, null);
    bGr.dispose();

    // Return the buffered image
    return bimage;
  }

  public static BufferedImage scaled(BufferedImage image, float scale) {
    int type = /*(image.getType() != 0) ? image.getType() :*/ BufferedImage.TYPE_INT_RGB;
    int ow = image.getWidth();
    int oh = image.getHeight();

    BufferedImage resized = null;
    float tenth = ow / 10.f;
    int steps = Math.max(1, (int) (Math.abs(ow - ow * scale) / tenth));

    int w = ow;
    int h = oh;
    for (int i = 1; i <= steps; i++) {
      float d = i / (float) steps;
      float scaleFactor = scale * d + 1 * (1 - d);

      int nw = (int) (ow * scaleFactor);
      int nh = (int) (oh * scaleFactor);

      resized = new BufferedImage(nw, nh, type);

      Graphics2D g = resized.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(image, 0, 0, nw, nh, 0, 0, w, h, null);
      g.dispose();

      image = resized;
      w = nw;
      h = nh;
    }

    return resized;

//    AffineTransform at = new AffineTransform();
//    at.scale(1 / scale, 1 / scale);
//    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//    return scaleOp.filter(image, resized);
  }

  public static BufferedImage thumbnail(String fileName, String thumbDir, Scaler scaler) throws IOException {
    BufferedImage scaled = null;

    File src = new File(fileName);
    Path thumbPath = (new File(thumbDir)).toPath();
    if (!thumbPath.toFile().isDirectory())
      thumbPath = src.toPath().resolveSibling(thumbDir);
    thumbPath = thumbPath.resolve(Strings.md5(fileName) + "-" + src.getName());

    File dst = thumbPath.toFile();
    if (dst.exists())
      try {
        scaled = ImageIO.read(dst);
      } catch (IOException e) {
        dst.delete();
        return thumbnail(fileName, thumbDir, scaler);
      }
    else
      try {
        BufferedImage srcImage = ImageIO.read(src);
        scaled = scaler.scaleImage(srcImage);

        dst.getParentFile().mkdirs();
        ImageIO.write(scaled, Strings.explode(src.getName(), "\\.").pop(), dst);
      } catch (IOException e) {

      }

    return scaled;
  }

  public interface Scaler {

    public BufferedImage scaleImage(BufferedImage image);

  }

}

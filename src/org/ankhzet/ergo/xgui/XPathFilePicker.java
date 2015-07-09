package org.ankhzet.ergo.xgui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import org.ankhzet.ergo.UILogic;
import org.ankhzet.ergo.files.Parser;
import org.ankhzet.ergo.utils.Strings;
import java.io.*;
import org.ankhzet.ergo.ClassFactory.IoC;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class XPathFilePicker extends CommonControl {

  protected Image[] ims = new Image[4];
  final int STATE_NORMAL = 0,
  STATE_OVERED = 1,
  STATE_PRESSED = 2,
  STATE_DISABLED = 3,
  ITEM_HEIGHT = 19;
//                            			!overed  						overed
//                            		!clk 			clk 			!clk 		clk
  private int[][] states = {{STATE_NORMAL, STATE_PRESSED}, {STATE_OVERED, STATE_PRESSED}};
  String caption = "";
  String root = "/";
  Strings dirs = new Strings();
  Strings files = new Strings();
  int fontHeight = 12;
  public int higlited = -1, selected = -1, aim = -1;
  boolean aiming = false;

  public XPathFilePicker(String caption) {
    super(-1000, 0, 0, 0);
    setActionListener(IoC.<UILogic>get(UILogic.class));
    try {
      Parser p = new Parser(UILogic.LocalDir + "/config/filepicker.cfg");

      int i = 0;
      p.checkNext("filepicker");
      p.checkNext("{");
      do {
        if (p.isToken("img"))
          ims[i++] = IoC.<UILogic>get(UILogic.class).loadImage("/" + p.getValue("=", ";"));

        if (!p.Token.equalsIgnoreCase("}"))
          p.checkNext(";");

      } while (!(p.Token.equalsIgnoreCase("}") || p.Token.isEmpty()));
      p.check("}");
      p.close();
    } catch (Throwable e) {
    }
    this.caption = caption;
  }

  @Override
  public void DoDraw(Graphics2D g) {
    g.clipRect(x, y, w, h - ITEM_HEIGHT);

    Font f = g.getFont();
    fontHeight = f.getSize();

    int dy = 0;// 3;
    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(x, y, w, h);
    int idx = 0;
    for (String dir : dirs) {
      drawItem(g, dir, y + dy, true, higlited == idx, selected == idx);
      dy += ITEM_HEIGHT;
      idx++;
    }
    for (String file : files) {
      drawItem(g, file, y + dy, false, higlited == idx, selected == idx);
      dy += ITEM_HEIGHT;
      idx++;
    }

    g.setClip(null);
    g.setColor(Color.GRAY);
    g.drawString(root, x, y + h - ITEM_HEIGHT + fontHeight + (ITEM_HEIGHT - fontHeight) / 2 - 1);
  }

  private void drawItem(Graphics2D g, String caption, int ty, boolean dir, boolean higlited, boolean selected) {
    int tw = w - (selected ? ITEM_HEIGHT : 0) - 1;

    if (higlited) {
      g.setColor(!higlited ? Color.LIGHT_GRAY : Color.GRAY);
      g.fillRoundRect(x, ty, tw, ITEM_HEIGHT, 6, 6);
    }
//    g.setColor(Color.GRAY);
//    g.drawRoundRect(x + 1, ty + 1, tw - 2, ITEM_HEIGHT, 6, 6);
    g.setColor(Color.BLACK);
    g.drawRoundRect(x, ty, tw, ITEM_HEIGHT, 6, 6);
    g.setColor(Color.WHITE);
    g.drawRoundRect(x + 1, ty + 1, tw - 2, ITEM_HEIGHT - 2, 6, 6);
//      g.setColor(Color.WHITE);
//      g.drawString(dir, tx + 5, ty + ch + (th - ch) / 2);
    g.setColor(selected ? Color.WHITE : Color.BLACK);
    g.drawString(caption, x + 5, ty + fontHeight + (ITEM_HEIGHT - fontHeight) / 2 - 1);

    if (selected) {
      g.setColor(!(aiming && higlited) ? Color.LIGHT_GRAY : Color.GRAY);
      g.fillRoundRect(x + tw, ty, ITEM_HEIGHT, ITEM_HEIGHT, 6, 6);
//      g.setColor(Color.GRAY);
//      g.drawRoundRect(x + tw + 1, ty + 1, ITEM_HEIGHT - 2, ITEM_HEIGHT, 6, 6);
      g.setColor(Color.BLACK);
      g.drawRoundRect(x + tw, ty, ITEM_HEIGHT, ITEM_HEIGHT, 6, 6);
      g.setColor(Color.WHITE);
      g.drawRoundRect(x + tw + 1, ty + 1, ITEM_HEIGHT - 2, ITEM_HEIGHT - 2, 6, 6);
    }
  }

  void fetchRoot() {
    dirs.clear();
    files.clear();

    File path = new File(root);
    if (!path.isDirectory())
      return;

    try {
      root = path.getCanonicalPath();
    } catch (IOException ex) {
    }

    File[] filesList = path.listFiles(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        return !name.matches("^\\..*$");
      }
    });
    for (File entry : filesList)
      if (entry.isDirectory())
        dirs.add(entry.getName());
      else
        files.add(entry.getName());


  }

  public void setRoot(String root) {
    this.root = root;
    fetchRoot();
    dirs.add(0, "..");
    selected = -1;
    higlited = -1;
    aim = -1;
  }

  public void setList(Strings list) {
    this.root = "/";
    dirs.clear();
    files.clear();
    for (String s : list)
      dirs.add(s);
    selected = -1;
    higlited = -1;
    aim = -1;
  }

  @Override
  public boolean mouseEvent(MouseEvent e, boolean process) {
    overed = ptInRect(e.getX(), e.getY());
    if (!process) {
      clicked = false;
      return overed;
    }

    if (!enabled)
      return false;

    int mx = e.getX() - x;
    int my = e.getY() - y - 3;

    higlited = (my < 0) || (my / ITEM_HEIGHT >= dirs.size() + files.size()) ? -1 : my / ITEM_HEIGHT;
    aiming = mx > (w - 3 - ITEM_HEIGHT) && mx < w - 3;

    switch (e.getID()) {
    case MouseEvent.MOUSE_RELEASED:
      if (clicked && overed)
        doClick();
      clicked = false;
      aim = selected;
      break;
    case MouseEvent.MOUSE_PRESSED:
      clicked = overed;
      if (clicked) {
        selected = higlited;
        aiming = false;
      }
      break;
    }
    return clicked;
  }

  @Override
  public void doClick() {
    String entry = getSelected();
    if (aim >= 0 && aiming && entry != null && !selectedFile())
      setRoot(root + File.separator + entry);
  }

  public String getSelected() {
    int ds = dirs.size();
    return aim < 0 ? null
    : aim >= ds ? files.get(aim - ds) : dirs.get(aim);
  }

  public String getSelectedPath() {
    String child = getSelected();
    String path = root;
    if (child.equals("..")) {
      Strings parts = Strings.explode(path, File.separator);
      if (parts.pop() != null)
        return parts.join(File.separator);
      else
        return root;
    } else
      return root + File.separator + child;
  }

  public boolean hasSelected() {
    return aim >= 0;
  }

  public boolean selectedFile() {
    return aim >= dirs.size();
  }
}

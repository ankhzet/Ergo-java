package org.ankhzet.ergo.ui.xgui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.files.Parser;
import org.ankhzet.ergo.utils.Strings;
import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.ui.xgui.filepicker.CollumnedItemVisitor;
import org.ankhzet.ergo.ui.xgui.filepicker.PickedNode;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class XPathFilePicker extends CommonControl {

  class FilesList extends ArrayList<File> {
  };

  protected Image[] ims = new Image[4];
  final int STATE_NORMAL = 0,
          STATE_OVERED = 1,
          STATE_PRESSED = 2,
          STATE_DISABLED = 3,
          ITEM_HEIGHT = 19;

  String caption = "";
  String root = "/";
  FilesList dirs = new FilesList();
  FilesList files = new FilesList();
  int fontHeight = 12;
  public File higlited, selected, aiming;

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

  String itemCaption(File item) {
    return root.equals("/") ? item.getPath() : item.getName();
  }

  @Override
  public void DoDraw(Graphics2D g) {
    g.translate(x, y);
    g.clipRect(0, 0, w, h - ITEM_HEIGHT);
    Shape clip = g.getClip();

    Font f = g.getFont();
    fontHeight = f.getSize();

    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(0, 0, w, h);

    CollumnedItemVisitor.NodeVisitor<File> nodeVisitor = (Rectangle r, File item) -> {
      boolean isHilited = item.equals(higlited);
      boolean isSelected = item.equals(selected);
      boolean isAiming = item.equals(aiming);
      boolean showBtn = isAiming && item.isDirectory();
      int c = 6;

      g.setClip(clip);
      g.clipRect(r.x, r.y, r.width, r.height + 1);
      int tw = r.width - (showBtn ? r.height : 0) - 1;

      if (isHilited) {
        g.setColor(Color.GRAY);
        g.fillRoundRect(r.x, r.y, tw, r.height, c, c);
      }
      g.setColor(Color.BLACK);
      g.drawRoundRect(r.x, r.y, tw, r.height, c, c);
      g.setColor(Color.WHITE);
      g.drawRoundRect(r.x + 1, r.y + 1, tw - 2, r.height - 2, c, c);
      g.setColor(isSelected ? Color.WHITE : Color.BLACK);
      g.drawString(itemCaption(item), r.x + 5, r.y + fontHeight + (r.height - fontHeight) / 2 - 1);

      if (showBtn) {
        g.setColor(!(isAiming && isHilited) ? Color.LIGHT_GRAY : Color.GRAY);
        g.fillRoundRect(r.x + tw, r.y, r.height, r.height, c, c);
        g.setColor(Color.BLACK);
        g.drawRoundRect(r.x + tw, r.y, r.height, r.height, c, c);
        g.setColor(Color.WHITE);
        g.drawRoundRect(r.x + tw + 1, r.y + 1, r.height - 2, r.height - 2, c, c);
      }

      return false;
    };

    CollumnedItemVisitor<File> v = itemVisitor(w, h);
    v.walkItems(dirs, nodeVisitor);
    v.walkItems(files, nodeVisitor);

    g.setClip(null);
    g.setColor(Color.GRAY);
    g.drawString(root, 0, h - ITEM_HEIGHT + fontHeight + (ITEM_HEIGHT - fontHeight) / 2 - 1);

    g.translate(-x, -y);
  }

  PickedNode<File> itemUnderXY(int w, int h, int x, int y) {
    CollumnedItemVisitor<File> v = itemVisitor(w, h);

    FilesList l = new FilesList();
    l.addAll(dirs);
    l.addAll(files);
    return v.walkItems(l, (Rectangle r, File f) -> {
      return r.contains(x, y);
    });
  }

  int rowsInView() {
    return (int) ((h - ITEM_HEIGHT) / ITEM_HEIGHT);
  }

  float columnWidth() {
    int total = dirs.size() + files.size();
    int r = rowsInView();
    if (r >= total)
      return w;

    int fitts = Math.max(1, w / 140);
    int columns = fitts;
    while (true) {
      int holds = r * fitts;
      if (holds < total)
        break;

      columns = fitts--;
    }
    return w / (float) columns;
  }

  CollumnedItemVisitor<File> itemVisitor(int w, int h) {
    return new CollumnedItemVisitor<>(columnWidth(), ITEM_HEIGHT, 0, rowsInView());
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

    File[] filesList = path.listFiles((File dir, String name) -> !name.matches("^\\..*$"));
    for (File entry : filesList)
      if (entry.isDirectory())
        dirs.add(entry);
      else
        files.add(entry);
  }

  public void setRoot(String root) {
    this.root = root;
    fetchRoot();
    dirs.add(0, new File(".."));
    selected = null;
    higlited = null;
    aiming = null;
  }

  public void setList(Strings list) {
    this.root = "/";
    dirs.clear();
    files.clear();
    list.forEach((s) -> dirs.add(new File(s)));
    selected = null;
    higlited = null;
    aiming = null;
  }

  @Override
  public boolean mouseEvent(MouseEvent e, boolean process) {
    if (!visible)
      return false;

    overed = ptInRect(e.getX(), e.getY());
    if (!process) {
      clicked = false;
      return overed;
    }

    if (!isEnabled())
      return false;

    int mx = e.getX() - x;
    int my = e.getY() - y - 3;

    higlited = null;
    boolean clickedBtn = false;
    PickedNode<File> p = itemUnderXY(w, h, mx, my);
    if (p != null) {
      higlited = p.node;
      if (higlited.isDirectory())
        if (mx - p.r.x > (p.r.width - 3 - ITEM_HEIGHT) && mx - p.r.x < p.r.width - 3)
          clickedBtn = true;
    }

    switch (e.getID()) {
      case MouseEvent.MOUSE_RELEASED:
        if (clicked && overed)
          aim(clickedBtn);

        clicked = false;
        break;
      case MouseEvent.MOUSE_PRESSED:
        clicked = overed;
        if (clicked) {
          if (selected != higlited)
            aiming = null;
          selected = higlited;
        }
        break;
    }
    return clicked;
  }

  void aim(boolean btn) {
    if (btn && aiming != null)
      doClick();
    else
      aiming = (selected == higlited) ? selected : null;
  }

  @Override
  public void doClick() {
    String path = getFilePath(aiming);
    if (aiming.isDirectory())
      setRoot(path);
  }

  public String getSelected() {
    return (selected != null) ? selected.getName() : null;
  }

  File expandFile(File f) {
    if (f != null)
      if (!f.getName().equals(".."))
        return f;
      else
        return (new File(root)).getParentFile();

    return f;
  }

  public File getSelectedFile() {
    return expandFile(selected);
  }

  public String getFilePath(File f) {
    f = expandFile(f);
    if (f == null)
      f = new File(root);
    return f.getPath();
  }

  public String getSelectedPath() {
    return getFilePath(getSelectedFile());
  }

  public boolean hasSelected() {
    return selected != null;
  }

  public boolean selectedFile() {
    return hasSelected() && selected.isFile();
  }

}

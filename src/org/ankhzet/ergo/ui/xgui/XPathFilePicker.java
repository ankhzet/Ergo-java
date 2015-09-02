package org.ankhzet.ergo.ui.xgui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.ankhzet.ergo.ConfigParser;
import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.files.Parser;
import org.ankhzet.ergo.ui.Skin;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.ui.xgui.filepicker.CollumnedItemVisitor;
import org.ankhzet.ergo.ui.xgui.filepicker.FilesList;
import org.ankhzet.ergo.ui.xgui.filepicker.PickedNode;
import org.ankhzet.ergo.utils.Strings;

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

  String caption = "";
  String root = "/";
  protected FilesList entries = new FilesList();
  public File higlited, selected, aiming;

  UILogic uil;

  public XPathFilePicker(String caption) {
    super(-1000, 0, 0, 0);
    uil = IoC.get(UILogic.class);
    setActionListener(uil);
    try {
      Parser p = IoC.make(ConfigParser.class, "filepicker");

      int i = 0;
      p.checkNext("filepicker");
      p.checkNext("{");
      do {
        if (p.isToken("img"))
          ims[i++] = uil.loadImage("/" + p.getValue("=", ";"));

        if (!p.Token.equalsIgnoreCase("}"))
          p.checkNext(";");

      } while (!(p.Token.equalsIgnoreCase("}") || p.Token.isEmpty()));
      p.check("}");
      p.close();
    } catch (Throwable e) {
    }
    this.caption = caption;
  }

  public String itemCaption(File item) {
    return root.equals("/") ? item.getPath() : item.getName();
  }

  public File getRootFile() {
    return new File(root);
  }

  @Override
  public void DoDraw(Graphics2D g) {
    g.translate(x, y);
    g.clipRect(0, 0, w, h - ITEM_HEIGHT);

//    g.setColor(Skin.get().BG_COLOR);
//    g.fillRect(0, 0, w, h);
    drawItems(g);

    int fontHeight = g.getFont().getSize();
    g.setClip(null);
    g.setColor(Color.GRAY);
    g.drawString(root, 0, h - ITEM_HEIGHT + fontHeight + (ITEM_HEIGHT - fontHeight) / 2 - 1);

    g.translate(-x, -y);
  }

  public void drawItems(Graphics2D g) {
    Shape clip = g.getClip();
    int fontHeight = g.getFont().getSize();
    CollumnedItemVisitor.NodeVisitor<File> nodeVisitor = (Rectangle r, File item) -> {
      boolean isHilited = (higlited == item);// && item.equals(higlited);
      boolean isSelected = (selected == item);// && item.equals(selected);
      boolean isAiming = (aiming == item);// && item.equals(aiming);
      boolean showBtn = isAiming && item.isDirectory();
      g.setClip(clip);
      g.clipRect(r.x, r.y, r.width, r.height + 1);
      r = (Rectangle) r.clone();
      r.grow(-1, -1);
      int btnWidth = r.height;
      int tw = r.width - (showBtn ? btnWidth : 0) - 1;
      if (isHilited) {
        g.setColor(Color.GRAY);
        Skin.fillBevel(g, r.x, r.y, r.width, r.height);
      }
      Skin.drawBevel(g, r.x, r.y, tw, r.height);
      if (showBtn) {
        Skin.drawBevel(g, r.x + tw, r.y, btnWidth, r.height);
        r.grow(-btnWidth / 2, 0);
        r.translate(-btnWidth / 2, 0);
      }
      r.grow(-8, 0);
      g.clipRect(r.x, r.y, r.width, r.height + 1);
      g.setColor(Color.LIGHT_GRAY);
      g.drawString(itemCaption(item), r.x, 1 + r.y + fontHeight + (r.height - fontHeight) / 2 - 1);
      g.setColor(isSelected ? Color.BLUE : Color.BLACK);
      g.drawString(itemCaption(item), r.x, r.y + fontHeight + (r.height - fontHeight) / 2 - 1);
      return false;
    };
    CollumnedItemVisitor<File> v = itemVisitor(w, h);
    v.walkItems(entries, nodeVisitor);
  }

  PickedNode<File> itemUnderXY(int w, int h, int x, int y) {
    CollumnedItemVisitor<File> v = itemVisitor(w, h);

    return v.walkItems(entries, (Rectangle r, File f) -> {
      return r.contains(x, y);
    });
  }

  int rowsInView() {
    return ((h - ITEM_HEIGHT) / ITEM_HEIGHT);
  }

  float columnWidth() {
    int total = entries.size();
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

  protected CollumnedItemVisitor<File> itemVisitor(int w, int h) {
    return new CollumnedItemVisitor<>(columnWidth(), ITEM_HEIGHT, 0, rowsInView());
  }

  protected File upFolderFile() {
    return new File("..");
  }

  protected void fetchRoot() {
    entries.clear();
    entries.add(upFolderFile());

    File path = getRootFile();
    if (!path.isDirectory())
      return;

    try {
      root = path.getCanonicalPath();
    } catch (IOException ex) {
      root = path.getPath();
    }

    File[] filesList = path.listFiles((File dir, String name) -> !name.matches("^\\..*$"));
    entries.addAll(Arrays.asList(filesList));
  }

  public void setRoot(String root) {
    this.root = root;
    fetchRoot();
    selected = null;
    higlited = null;
    aiming = null;
  }

  public void setList(Strings list) {
    this.root = "/";
    entries.clear();
    list.forEach((s) -> entries.add(new File(s)));
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
      if (!f.getName().equals(upFolderFile().getName()))
        return f;
      else
        return getRootFile().getParentFile();

    return f;
  }

  public File getSelectedFile() {
    return expandFile(selected);
  }

  public String getFilePath(File f) {
    f = expandFile(f);
    if (f == null)
      f = getRootFile();
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

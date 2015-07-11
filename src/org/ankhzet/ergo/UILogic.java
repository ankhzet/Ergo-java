/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ankhzet.ergo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.xgui.XActionListener;
import org.ankhzet.ergo.xgui.XButton;
import org.ankhzet.ergo.xgui.XControls;
import org.ankhzet.ergo.pages.UIHomePage;


/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UILogic implements Runnable, XActionListener, LoaderProgressListener {

  public static UILogic instance;
  static UIContainerListener container;
  public static Toolkit toolkit;
  private Thread thread = null;
  private Image backbuffer = null;
  private Graphics2D backgraphics = null;
//  private Vector<Cursor> cursors = new Vector<Cursor>();
//  private Cursor defCursor = null;
  public static String LocalDir = "";
  public static final int MSGBOX_TIMEOUT = 2000;
  private long threaddelay = 15;
  public Rectangle clientArea = new Rectangle();
  XControls hud = null;
//  Point cursor = new Point(0, 0);
  boolean initiated = false;
  long msgboxTimeout = 0;
  public static final int UIPANEL_HEIGHT = 30;
  static final int THREAD_DELAY_IDDLE = 60;
  static final int THREAD_DELAY_ANIMATE = 5;
  ProgressRenderer progress = new ProgressRenderer();
  String tooltip = null;
  int tooltipX, tooltipY;
  UIPage currentUI, prevUI = null;
  String actionToPerform = null;

  public UILogic() {
    toolkit = Toolkit.getDefaultToolkit();
  }

  public void paint(Graphics g) {
    int w = clientArea.width;
    int h = clientArea.height;
    try {
      if (backbuffer == null) {
        backbuffer = new BufferedImage(w + 4, h + 4, BufferedImage.TYPE_INT_RGB);
        backgraphics = (Graphics2D) (backbuffer.getGraphics());
        Font f = new Font("default", Font.PLAIN, 11);
        backgraphics.setFont(f);
        backgraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        backgraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }
    } catch (Throwable e) {
      backbuffer = null;
      backgraphics = null;
      return;
    }
    if (!initiated || backgraphics == null)
      return;

    backgraphics.setColor(Skin.get().BG_COLOR);
    backgraphics.fillRect(2, 2, w, h);
    backgraphics.setColor(Color.BLACK);

    backgraphics.translate(2, 2);
    draw(backgraphics);
    backgraphics.translate(-2, -2);

    backgraphics.setColor(Color.WHITE);
    backgraphics.drawRect(1, 1, w + 1, h + 1);
    backgraphics.setColor(Color.BLACK);
    backgraphics.drawRect(0, 0, w + 3, h + 3);

    backbuffer.flush();
    g.drawImage(backbuffer, clientArea.x - 2, clientArea.y - 2, clientArea.width + 4, clientArea.height + 4, null);
  }

  @Override
  public void run() {
    init();
    long t = System.currentTimeMillis();
    long t2 = t;
    while (Thread.currentThread() == thread) {
      container.repaint();
      try {
        if (t2 - t >= threaddelay) {
          t = t2;
          process();
        }
        Thread.sleep(threaddelay);
      } catch (InterruptedException e) {
        stop();
      }
      t2 = System.currentTimeMillis();
    }
  }

  public void start() {
    LocalDir = System.getProperty("user.home") + "/.ergo";
    container.requestFocus();
//    defCursor = setCursor("default");
    thread = new Thread(this);
    thread.start();
  }

  public void stop() {
    thread = null;
  }

  public Image loadImage(String src) {
    return toolkit.getImage(LocalDir + src);
  }

// <editor-fold defaultstate="collapsed" desc="cursor handling">
/*  public Cursor loadCursor(String src, int hotx, int hoty, String name) {
  Cursor c = toolkit.createCustomCursor(loadImage(src), new java.awt.Point(hotx, hoty), name);
  cursors.add(c);
  return c;
  }

  public Cursor setCursor(String name) {
  for (Cursor c : cursors)
  if (c.getName().equalsIgnoreCase(name))
  //container.setCursor(c);
  return c;

  Cursor c = loadCursor("/res/" + name + ".png", 1, 1, name);
  c = (c == null) ? defCursor : c;
  if (c != null) //container.setCursor(c)
  ;
  return c;
  }
   */
// </editor-fold>
  public static void log(String format, Object... args) {
    System.out.printf(format, args);
    System.out.println();
  }

  public void resize(int x, int y, int w, int h) {
    clientArea.setBounds(x + 2, y + 2, w - 4, h - 4);
    backbuffer = null;
    backgraphics = null;
    if (currentUI != null)
      currentUI.resized(clientArea.x, clientArea.y, clientArea.width, clientArea.height);

    hud.pack(clientArea.width, clientArea.height);
  }

  void msgBox(Graphics2D g, String s) {
    Font f = g.getFont();
    FontRenderContext frc = g.getFontRenderContext();
    Rectangle2D r;
    String c, t;
    int ch = f.getSize();
    int th = 0, tp = 0, rw = 0, rh = 0, tw = 0;
    c = s.replaceAll("(^[\10\40\09]+)|([\10\09\40]+$)", "");
    while (!c.isEmpty()) {
      th += ch;
      int cp = c.indexOf('\n');
      if (cp < 0)
        cp = c.length() - 1;

      r = f.getStringBounds(c, 0, cp, frc);
      int cw = (int) r.getWidth();
      if (cw > tw)
        tw = cw;

      if (c.length() - cp > 1)
        c = c.substring(cp + 1);
      else
        break;
    }

    rw = tw + 100;
    if (rw < 200)
      rw = 200;

    int x = clientArea.x;
    int y = clientArea.y;
    int w = clientArea.width;
    int h = clientArea.height;

    rh = th + 100;
    g.setColor(Color.LIGHT_GRAY);
    g.fillRect(x + (w - rw) / 2, y + (h - rh) / 2, rw, rh);
    g.setColor(Color.BLACK);
    g.drawRect(x + (w - rw) / 2, y + (h - rh) / 2, rw, rh);

    while (!s.isEmpty()) {
      tp += ch;
      int len = s.length(), cp = s.indexOf('\n');
      if (cp < 0)
        cp = len;

      t = s.substring(0, cp);
      r = f.getStringBounds(t, frc);
      int cw = (int) r.getWidth();
      g.drawString(t, x + (w - cw) / 2, y + (h - th) / 2 + tp);

      if (len - cp > 1)
        s = s.substring(cp + 1);
      else
        break;
    }
  }

  public boolean mouseEvent(MouseEvent e) {
    if (!initiated)
      return false;

    e.translatePoint(-clientArea.x, -clientArea.y);
    if (hud.mouseEvent(e))
      return true;

    if (System.currentTimeMillis() - msgboxTimeout < MSGBOX_TIMEOUT)
      return false;

    int mx = e.getX();
    int my = e.getY();

    Rectangle r = new Rectangle(0, 0, clientArea.width, UIPANEL_HEIGHT);

    if (r.contains(mx, my)) {// || (mx >= clientArea.width) || (my >= clientArea.height)) {
      currentUI.mouseDown = false;
      return false;
    }

    e.translatePoint(0, -UIPANEL_HEIGHT);
    currentUI.mouseEvent(e);
    process();
    return true;
  }

  void init() {
    msgboxTimeout = 0;
    hud = new XControls();
    navigateTo(UIHomePage.class);
    initiated = true;
  }

  public UIPage navigateTo(Class c, Object... params) {
    prevUI = currentUI;

    currentUI = (UIPage)IoC.<UIPage>get(c);

    if (currentUI == null)
      currentUI = prevUI;

    if (prevUI == currentUI)
      return currentUI;

    if (prevUI != null)
      prevUI.navigateOut();

    currentUI.navigateIn(params);
    int l = hud.onLeft();
    while (l++ < 2)
      hud.putControl(new XButton("", null, "xspacer"), XControls.AREA_LEFT);

    hud.putControl(new XButton("", null, "xmover"), XControls.AREA_LEFT);
    hud.putControl(new XButton("exit", "Выход", "xbutton"), XControls.AREA_LEFT);

    hud.pack(clientArea.width, clientArea.height);
    currentUI.resized(clientArea.x, clientArea.y, clientArea.width, clientArea.height);
    return currentUI;
  }

  public boolean softNavigateBack() {
    if (prevUI != null) {
      navigateTo(prevUI.getClass());
      return true;
    }
    return false;
  }

  void process() {
    if (actionToPerform != null) {
      String action = actionToPerform;
      actionToPerform = null;
      if (currentUI.actionPerformed(action))
        return;

      if (action.equals("exit"))
        System.exit(0);
      else
        if (action.equals("back"))
          softNavigateBack();
    }
    hud.Process();
    currentUI.process();
  }

  public void intensiveRepaint(boolean intensive) {
    threaddelay = intensive ? THREAD_DELAY_ANIMATE : THREAD_DELAY_IDDLE;
  }

  @Override
  public void actionPerformed(String a) {
    actionToPerform = a;
  }

  public XControls getHUD() {
    return hud;
  }

  void draw(Graphics2D g) {
    int w = clientArea.width;
    int h = clientArea.height;

    g.translate(0, UIPANEL_HEIGHT);
    currentUI.draw(g, w, h - UIPANEL_HEIGHT);
    g.translate(0, -UIPANEL_HEIGHT);

    try {
      hud.Draw(g, 0, 0, w, UIPANEL_HEIGHT);
    } catch (Exception e) {
    }

    progress.draw(g, w, h);

    if (tooltip != null) {
      Font f = g.getFont();
      FontRenderContext frc = g.getFontRenderContext();
      Rectangle2D r = f.getStringBounds(tooltip, frc);
      int tw = (int) r.getWidth() + 6;
      int tx = tooltipX - tw / 2;
      tx = tx < 0 ? 0 : (tx + tw - 1 >= w ? w - tw - 1 : tx);
      int ty = tooltipY + 3;
      int th = (int) r.getHeight() + 6;
      int ch = (int) r.getY();
      g.setColor(Color.LIGHT_GRAY);
      g.fillRoundRect(tx, ty, tw, th, 6, 6);
      g.setColor(Color.GRAY);
      g.drawRoundRect(tx + 1, ty + 1, tw - 2, th, 6, 6);
      g.setColor(Color.BLACK);
      g.drawRoundRect(tx, ty, tw, th, 6, 6);
      g.setColor(Color.WHITE);
      g.drawRoundRect(tx + 1, ty + 1, tw - 2, th - 2, 6, 6);
      g.setColor(Color.WHITE);
      g.drawString(tooltip, tx + 4, ty - ch + 4);
      g.setColor(Color.DARK_GRAY);
      g.drawString(tooltip, tx + 4, ty - ch + 3);
    }
// todo: msgbox render
  }

  @Override
  public boolean onProgress(int state, int p, int max) {
    if (p >= max) {
      progress.hide();
      return true;
    }
    progress.setProgress(LoaderProgressListener.LABELS[state], p, max);
    return currentUI.onProgress(state, p, max);
  }

  @Override
  public void progressDone() {
    progress.hide();
  }

  public void tooltip(String text, int x, int y) {
    tooltip = text;
    tooltipX = x;
    tooltipY = y;
  }
}



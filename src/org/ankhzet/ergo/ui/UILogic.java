/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ankhzet.ergo.ui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.ankhzet.ergo.App;
import org.ankhzet.ergo.Config;
import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.classfactory.annotations.DependencyInjection;
import org.ankhzet.ergo.ui.pages.UIPage;
import org.ankhzet.ergo.ui.pages.home.UIHomePage;
import org.ankhzet.ergo.ui.xgui.XAction;
import org.ankhzet.ergo.ui.xgui.XActionListener;
import org.ankhzet.ergo.ui.xgui.XControls;
import org.ankhzet.ergo.ui.xgui.XMessageBox;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UILogic implements Runnable, XActionListener, LoaderProgressListener {

  static final String kExitAction = "exit";
  static final String kExitLabel = "Exit";

  @DependencyInjection()
  UIContainerListener container;

  @DependencyInjection()
  protected Toolkit toolkit;

  @DependencyInjection()
  protected Config config;

  @DependencyInjection()
  XMessageBox msgBox;

  @DependencyInjection()
  protected XControls hud;

  private Thread thread = null;
  private Image backbuffer = null;
  private Graphics2D backgraphics = null;
  private long threaddelay = 15;
  public Rectangle clientArea = new Rectangle();
  boolean initiated = false;
  public static final int UIPANEL_HEIGHT = 30;
  static final int THREAD_DELAY_IDDLE = 30;
  static final int THREAD_DELAY_ANIMATE = 5;
  ProgressRenderer progress = new ProgressRenderer();
  String tooltip = null;
  int tooltipX, tooltipY;
  UIPage currentUI, prevUI = null;
  XAction actionToPerform = null;

  int RENDER_INSET = 5;
  int RENDER_INSET2 = RENDER_INSET * 2;

  boolean checkBackBuffer() {
    try {
      if (backbuffer == null) {
        backbuffer = new BufferedImage(clientArea.width + RENDER_INSET2, clientArea.height + RENDER_INSET2, BufferedImage.TYPE_INT_RGB);
        backgraphics = (Graphics2D) (backbuffer.getGraphics());
        Font f = new Font("default", Font.PLAIN, 11);
        backgraphics.setFont(f);
        backgraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        backgraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }
      return backgraphics != null;
    } catch (Throwable e) {
      backbuffer = null;
      backgraphics = null;
      return false;
    }
  }

  public void paint(Graphics g) {
    if (!(initiated && checkBackBuffer()))
      return;

    Rectangle r = new Rectangle(clientArea.getSize());
    r.grow(RENDER_INSET - 2, RENDER_INSET - 2);
    r.translate(RENDER_INSET, RENDER_INSET);

    backgraphics.setColor(Skin.get().BG_COLOR);
    backgraphics.fillRect(r.x, r.y, r.width, r.height);
    backgraphics.setColor(Color.BLACK);

    backgraphics.translate(RENDER_INSET, RENDER_INSET);
    draw(backgraphics);
    backgraphics.translate(-RENDER_INSET, -RENDER_INSET);

    r.grow(1, 1);
    backgraphics.setColor(Color.BLACK);
    backgraphics.drawRect(r.x, r.y, r.width - 1, r.height - 1);
    r.grow(1, 1);
    backgraphics.setColor(Color.LIGHT_GRAY);
    backgraphics.drawRect(r.x, r.y, r.width - 1, r.height - 1);

    backbuffer.flush();
    g.drawImage(backbuffer, clientArea.x - RENDER_INSET, clientArea.y - RENDER_INSET, null);

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
    container.requestFocus();
    thread = new Thread(this);
    thread.start();
  }

  public void stop() {
    thread = null;
  }

  public Image loadImage(String src) {
    return toolkit.getImage(App.appDir(src));
  }

  public static void log(String format, Object... args) {
    System.out.printf(format, args);
    System.out.println();
  }

  public void resize(int x, int y, int w, int h) {
    clientArea.setBounds(x + RENDER_INSET, y + RENDER_INSET, w - RENDER_INSET2, h - RENDER_INSET2);
    backbuffer = null;
    backgraphics = null;
    if (currentUI != null)
      currentUI.resized(clientArea.x, clientArea.y, clientArea.width, clientArea.height);

    hud.pack(clientArea.width, clientArea.height);
  }

  public boolean mouseEvent(MouseEvent e) {
    if (!initiated)
      return false;

    e.translatePoint(-clientArea.x, -clientArea.y);

    if (msgBox.isShown())
      return false;

    if (hud.mouseEvent(e))
      return true;

    int mx = e.getX();
    int my = e.getY();

    Rectangle r = new Rectangle(0, 0, clientArea.width, UIPANEL_HEIGHT);

    if (r.contains(mx, my)) {
      currentUI.setFocused(false);
      return false;
    }

    e.translatePoint(0, -UIPANEL_HEIGHT);
    return currentUI.mouseEvent(e);
  }

  public void keyEvent(KeyEvent e) {
    hud.keyEvent(e);
  }

  void init() {
    intensiveRepaint(false);
    navigateTo(UIHomePage.class);
    initiated = true;
  }

  public UIPage navigateTo(Class<? extends UIPage> c, Object... params) {
    prevUI = currentUI;

    currentUI = IoC.get(c);

    if (currentUI == null)
      currentUI = prevUI;

    if (prevUI == currentUI)
      return currentUI;

    if (prevUI != null)
      prevUI.navigateOut();

    hud.clear();
    if (prevUI != null)
      hud.putBackAction(XControls.AREA_LEFT, null);

    currentUI.resized(clientArea.x, clientArea.y, clientArea.width, clientArea.height);

    currentUI.navigateIn(params);
    int l = hud.onLeft();
    while (l++ < 2)
      hud.putSpacer(XControls.AREA_LEFT);

    hud.putMover(XControls.AREA_LEFT);
    hud.putActionAtLeft(kExitLabel, currentUI.registerAction(kExitAction, action -> System.exit(0)));

    hud.pack(clientArea.width, clientArea.height);
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
      XAction action = actionToPerform;
      actionToPerform = null;
      if (currentUI.actionPerformed(action))
        return;

      if (action.isA(XControls.kBackAction))
        softNavigateBack();
    }
    hud.Process();
    currentUI.process();
  }

  public void intensiveRepaint(boolean intensive) {
    threaddelay = intensive ? THREAD_DELAY_ANIMATE : THREAD_DELAY_IDDLE;
  }

  @Override
  public void actionPerformed(XAction a) {
    actionToPerform = a;
  }

  public XControls getHUD() {
    return hud;
  }

  void draw(Graphics2D g) {
    int w = clientArea.width;
    int h = clientArea.height;

    if (currentUI != null) {
      g.translate(0, UIPANEL_HEIGHT);
      backgraphics.setClip(0, 0, w, h - UIPANEL_HEIGHT);
      currentUI.draw(g, w, h - UIPANEL_HEIGHT);
      backgraphics.setClip(null);
      g.translate(0, -UIPANEL_HEIGHT);

      drawCenteredString(new Point(w / 2, UIPANEL_HEIGHT / 4), clientArea, 0, g, currentUI.title(), true);
    }

    hud.Draw(g, 0, 0, w, UIPANEL_HEIGHT);

    progress.draw(g, w, h);

    if (tooltip != null) {
      int inset = 3;
      int inse2 = inset * 2;
      Point toolPos = new Point(tooltipX, tooltipY);
      Rectangle r = drawCenteredString(toolPos, clientArea, inset, g, tooltip, false);

      g.setColor(Color.LIGHT_GRAY);
      g.fillRoundRect(r.x, r.y, r.width, r.height, inse2, inse2);
      g.setColor(Color.GRAY);
      g.drawRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height, inse2, inse2);
      g.setColor(Color.BLACK);
      g.drawRoundRect(r.x, r.y, r.width, r.height, inse2, inse2);
      g.setColor(Color.WHITE);
      g.drawRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2, inse2, inse2);
      drawCenteredString(toolPos, clientArea, inset, g, tooltip, true);
    }

    msgBox.draw(g, clientArea);
  }

  @Override
  public boolean onProgress(int state, int p, int max) {
    if (p >= max) {
      progress.hide();
      return true;
    }
    progress.setProgress(LABELS[state], p, max);
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

  public void message(String text) {
    msgBox.show(text);
  }

  public void message(String text, int forTime) {
    msgBox.show(text, forTime);
  }

  Rectangle drawCenteredString(Point pos, Rectangle clip, int inset, Graphics2D g, String string, boolean draw) {
    Font f = g.getFont();
    FontRenderContext frc = g.getFontRenderContext();
    Rectangle2D r = f.getStringBounds(string, frc);
    int tw = (int) r.getWidth() + inset * 2;
    int tx = pos.x - tw / 2;
    tx = tx < clip.x ? clip.x : (tx + tw - 1 >= clip.width ? clip.width - tw - 1 : tx);

    int ty = pos.y + inset;
    int th = (int) r.getHeight() + inset * 2;

    if (draw) {
      int ch = (int) r.getY();
      g.setColor(Color.WHITE);
      g.drawString(string, tx + inset + 1, ty - ch + inset + 1);
      g.setColor(Color.DARK_GRAY);
      g.drawString(string, tx + inset + 1, ty - ch + inset);
    }

    return new Rectangle(tx, ty, tw, th);
  }

}

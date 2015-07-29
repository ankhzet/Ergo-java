package org.ankhzet.ergo.ui.xgui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.ui.UILogic;
import org.ankhzet.ergo.files.Parser;

public class XButton extends CommonControl {

  protected Image[] ims = new Image[4];
  final int STATE_NORMAL = 0,
          STATE_OVERED = 1,
          STATE_PRESSED = 2,
          STATE_DISABLED = 3;
//                            			!overed  						overed
//                            		!clk 			clk 			!clk 		clk
  private int[][] states = {{STATE_NORMAL, STATE_PRESSED}, {STATE_OVERED, STATE_PRESSED}};
  String caption = "";

  public XButton(XAction action, String caption, String src) {
    super(-1000, 0, 0, 0);
    setAction(action);
    setActionListener(IoC.<UILogic>get(UILogic.class));
    try {
      Parser p = new Parser(UILogic.LocalDir + "/config/" + src + ".cfg");

      int i = 0;
      p.checkNext("button");
      p.checkNext("{");
      do {
        if (p.isToken("img"))
          ims[i++] = IoC.<UILogic>get(UILogic.class).loadImage("/" + p.getValue("=", ";"));
        else
          if (p.isToken("w"))
            w = Integer.parseInt(p.getValue("=", ";"));
          else
            if (p.isToken("h"))
              h = Integer.parseInt(p.getValue("=", ";"));

        if (!p.Token.equalsIgnoreCase("}"))
          p.checkNext(";");

      } while (!(p.Token.equalsIgnoreCase("}") || p.Token.isEmpty()));
      p.check("}");
      p.close();
    } catch (Throwable e) {
    }
    this.caption = caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  @Override
  public void DoDraw(Graphics2D g) {
    boolean isEnabled = isEnabled();
    boolean isToggled = action.isToggled();

    Image i;
    if (isEnabled)
      i = ims[states[overed ? 1 : 0][clicked || isToggled ? 1 : 0]];
    else
      i = ims[STATE_DISABLED];

    int dx = 0;
    int dy = 0;
    g.setColor(Color.GRAY);
    if (isEnabled)
      switch (states[overed ? 1 : 0][(clicked || isToggled) ? 1 : 0]) {
      case STATE_NORMAL:
        break;
      case STATE_PRESSED:
//				dx = 2;
//				dy = 2;
      case STATE_OVERED:
        g.setColor(Color.BLACK);
        break;
      }
    else // disabled
    //      g.setColor(Color.LIGHT_GRAY)
    ;

    if (!g.drawImage(i, x + dx, y + dy, w, h, null))
//      System.out.println("img not ready")
    ;

    if (overed)
      IoC.<UILogic>get(UILogic.class).tooltip(caption, x + dx + w / 2, y + dy + h);
  }

}

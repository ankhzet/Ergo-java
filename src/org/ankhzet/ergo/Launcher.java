/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ankhzet.ergo;

import org.ankhzet.ergo.ui.UILogic;
import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.jdesktop.application.Application;
import org.ankhzet.ergo.classfactory.IoC;
import org.ankhzet.ergo.factories.IoCFactoriesRegistrar;
import org.ankhzet.ergo.ui.UIContainerListener;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Launcher extends Application {

  /**
   * @param args the command line arguments
   */
  JFrame mainFrame = null;
  UILogic ui = null;

  @Override
  protected void startup() {
    IoCFactoriesRegistrar.register();

    ui = IoC.get(UILogic.class);

    mainFrame = IoC.get(UIContainerListener.class);
    mainFrame.addWindowListener(new LauncherListener());
    mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    ui.start();

    mainFrame.setLocationByPlatform(true);
    mainFrame.setVisible(true);
    mainFrame.setSize(300, 500);
  }

  @Override
  protected void shutdown() {
    mainFrame.setVisible(false);
    ui.stop();
  }

  public static void main(String[] args) {
    Application.launch(Launcher.class, args);
  }

  private class LauncherListener extends WindowAdapter {

    @Override
    public void windowClosing(WindowEvent e) {
      exit();
    }

  }

}

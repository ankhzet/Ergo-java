package org.ankhzet.ergo.ui.xgui;

import java.util.HashMap;
import org.ankhzet.ergo.ui.xgui.XAction.Action;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class XActions {

  HashMap<String, XAction> actions = new HashMap<>();

  public XAction registerAction(String actionName, Action action) {
    XAction xAction = new XAction(actionName, action);
    actions.put(actionName, xAction);
    return xAction;
  }

  public void registerActions(String[] actionNames, Action action) {
    for (String actionName : actionNames)
      actions.put(actionName, new XAction(actionName, action));
  }

  public boolean performAction(XAction a) {
    XAction action = actions.get(a.name);

    return (action != null) && action.perform();
  }

}

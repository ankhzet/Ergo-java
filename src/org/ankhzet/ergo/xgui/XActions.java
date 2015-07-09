package org.ankhzet.ergo.xgui;

import java.util.HashMap;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class XActions {

  public interface Action {
    public void perform(String name);
  }

  HashMap<String, Action> actions = new HashMap<>();

  public void registerAction(String actionName, Action action) {
    actions.put(actionName, action);
  }

  public void registerActions(String[] actionNames, Action action) {
    for (String actionName : actionNames) {
      actions.put(actionName, action);    
    }
  }

  public Action performAction(String a) {
    Action action = actions.get(a);
    
    if (action != null)
      action.perform(a);
    
    return action;
  }

}

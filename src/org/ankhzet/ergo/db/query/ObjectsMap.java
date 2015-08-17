package org.ankhzet.ergo.db.query;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.ankhzet.ergo.utils.Objects;
import org.ankhzet.ergo.utils.Strings;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ObjectsMap extends LinkedHashMap<String, Object> {

  public ObjectsMap() {
    super();
  }

  public ObjectsMap(Map<String, Object> values) {
    super(values);
  }

  @Override
  public ObjectsMap put(String key, Object value) {
    super.put(key, value);
    return this;
  }

  public static ObjectsMap of(Strings keys, Objects values) {
    ObjectsMap map = new ObjectsMap();
    for (int i = 0; i < keys.size(); i++)
      map.put(keys.get(i), values.get(i));

    return map;
  }

  public static ObjectsMap of(Object[][] array) {
    return Arrays.stream(array).collect(
      Collectors.toMap(
        kv -> (String) kv[0],
        kv -> kv[1],
        (u, v) -> {
          throw new RuntimeException(String.format("Duplicated keys %s", u));
        },
        ObjectsMap::new
      )
    );
  }

}

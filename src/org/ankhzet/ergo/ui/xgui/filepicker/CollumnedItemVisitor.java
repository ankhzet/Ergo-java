
package org.ankhzet.ergo.ui.xgui.filepicker;

import java.awt.Rectangle;
import java.util.List;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <T> type of items, enumerated by this visitor
 */
public class CollumnedItemVisitor<T> extends Batch<T> {

  public interface NodeVisitor<T> {

    boolean visitNode(Rectangle r, T item);

  }

  interface BatchProcessor<T> {

    PickedNode<T> process(Batch<T> batch);

  }

  public CollumnedItemVisitor(float columnWidth, float columnHeight, int dy, int batch) {
    super(columnWidth, columnHeight, dy, batch);
  }

  public PickedNode<T> walkItems(List<T> items, NodeVisitor<T> visitor) {
    BatchProcessor<T> processor = (e) -> {
      e.r.x = (int) (e.columnWidth * e.column);
      e.r.y = e.dy;
      for (T item : e) {
        if (visitor.visitNode(e.r, item))
          return new PickedNode<>(r, item);
        e.r.y += e.r.height;
      }
      return null;
    };

    for (T item : items) {
      if (ready()) {
        PickedNode<T> picked = processor.process(this);
        if (picked != null)
          return picked;

        nextBatch();
      }

      add(item);
    }

    return isEmpty() ? null : processor.process(this);
  }

}
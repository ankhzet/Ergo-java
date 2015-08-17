package org.ankhzet.ergo.manga.chapter.page;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ReadOptions {

  protected static final int//
    BIT_MODE      = 0x000001,
    BIT_STRETCH   = 0x000002,
    BIT_ORIGINAL  = 0x000004,
    BIT_FITHEIGHT = 0x000008,
    BIT_ROTATE    = 0x000010,
    BIT_CLOCKWISE = 0x000020,
    BIT_HORISSWIPE= 0x000040,
    BIT_VALID     = 0x800000;

  protected int bits = 0;
  
  public ReadOptions() {
    toggle(BIT_VALID);
    toggle(BIT_STRETCH);
    toggle(BIT_FITHEIGHT);
    toggle(BIT_CLOCKWISE);
    toggle(BIT_HORISSWIPE);
  }

  public ReadOptions(boolean manhwaMode, boolean stretchToFit, boolean originalSize) {
    super();
    bitSet(BIT_MODE, manhwaMode);
    bitSet(BIT_STRETCH, stretchToFit);
    bitSet(BIT_ORIGINAL, originalSize);
  }
  
  public void setOptions(int hash) {
    bits = hash;
  }
  
  public boolean valid() {
    return valid(bits);
  }
  
  public static boolean valid(int bits) {
    return (bits & BIT_VALID) == BIT_VALID;
  }
  
  private boolean toggle(int mask) {
    boolean set = bitIsSet(mask);
    bitSet(mask, !set);
    return set;
  }

  public boolean toggleRotationToFit() {
    return toggle(BIT_ROTATE);
  }

  public boolean toggleOriginalSize() {
    return toggle(BIT_ORIGINAL);
  }

  public boolean toggleSwipeDir() {
    return toggle(BIT_HORISSWIPE);
  }
  
  public boolean swipeHorisontal() {
    return bitIsSet(BIT_HORISSWIPE);
  }

  public boolean manhwaMode() {
    return bitIsSet(BIT_MODE);
  }

  public boolean stretchToFit() {
    return bitIsSet(BIT_STRETCH);
  }

  public boolean originalSize() {
    return bitIsSet(BIT_ORIGINAL);
  }

  public boolean fitHeight() {
    return bitIsSet(BIT_FITHEIGHT);
  }

  public boolean rotateToFit() {
    return bitIsSet(BIT_ROTATE);
  }

  public boolean turnClockwise() {
    return bitIsSet(BIT_CLOCKWISE);
  }

  @Override
  public int hashCode() {
    return bits;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final ReadOptions other = (ReadOptions) obj;
    return this.bits == other.bits;
  }

  private boolean bitIsSet(long mask) {
    return (bits & mask) == mask;
  }

  private long bitSet(int mask, boolean set) {
    int applied = bits & (mask ^ 0xFFFFFFFF);
    if (set)
      applied |= mask;
    
    setOptions(applied);
    
    return applied;
  }

}

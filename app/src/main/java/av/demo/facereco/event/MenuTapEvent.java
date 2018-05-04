package av.demo.facereco.event;

/**
 * Created by Antonio Vitiello on 13/04/2018.
 */

public class MenuTapEvent {
    public static final int DETECT_FACE = 0;
    public static final int RECOGNIZE_DIR = 1;
    private final int mItemId;

    public MenuTapEvent(int itemId) {
        mItemId = itemId;
    }

    public int getmItemId() {
        return mItemId;
    }
}

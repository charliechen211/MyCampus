package com.stpi.campus;

public final class Configuration {
    public static int DISPLAY_NUM_OF_COLUMNS_IN_HORRIZONTAL = 2;
    public static int DISPLAY_NUM_OF_COLUMNS_IN_VERTICAL = 4;
    public static int DISPLAY_NUM_OF_COLUMNS_AUTO = -1;

    static public int getNumOfColumns() {
        return DISPLAY_NUM_OF_COLUMNS_AUTO;
    }
}

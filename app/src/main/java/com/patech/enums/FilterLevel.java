package com.patech.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pagrawal on 13-12-2017.
 */

public enum FilterLevel {
    None(0),
    EXCLUDED(1),
    All(2);

    int id;

    private static Map<Integer, FilterLevel> idToValueMap = new HashMap<>();
    static {
        for (FilterLevel level : values()) {
            idToValueMap.put(level.getId(), level);
        }
    }
    FilterLevel(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
    public static FilterLevel getFilterLevel(int id) {
        FilterLevel level = idToValueMap.get(id);
        if (level == null) {
            level = All;
        }
        return level;
    }

}

package io.eisner.ads;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Set;

/**
 * An easy way to store HTTP Header values for use when making requests
 * Created by nate eisner.
 */
public class HeaderFields {
    protected HashMap<String, String> headerMap;

    public HeaderFields() {
        headerMap = new HashMap<>();
    }

    public boolean add(@NonNull String name, @NonNull String value) {
        if (!headerMap.containsKey(name)) {
            headerMap.put(name, value);
            return true;
        } else
            return false;
    }

    public String[] getNameStrings() {
        return headerMap.keySet().toArray(new String[headerMap.size()]);
    }

    public Set<String> getNameSet() {
        return headerMap.keySet();
    }

    public String getValue(@NonNull String name) {
        return headerMap.get(name);
    }

    public void update(@NonNull String name, @NonNull String value) {
        headerMap.put(name, value);
    }
}

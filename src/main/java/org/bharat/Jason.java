package org.bharat;

public class Jason {
    private StringBuilder json;

    public Jason() {
        this.json = new StringBuilder("{\n");
    }

    public String serialize() {
        json.append("}\n");

        return json.toString();
    }

    public Jason addString(final String key, final String value) {
        json.append("\t\"" + key + "\": " + "\"" + value + "\",\n");

        return this;
    }
}

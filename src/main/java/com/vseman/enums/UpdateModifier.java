package com.vseman.enums;

/**
 * Created by Володя on 15.03.2015.
 */
public enum UpdateModifier {

    SET("set"), ADD("add"), REMOVE("remove"), REMOVEREGEX("removeregex");

    private final String modifier;

    UpdateModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getModifier() {
        return this.modifier;
    }
}

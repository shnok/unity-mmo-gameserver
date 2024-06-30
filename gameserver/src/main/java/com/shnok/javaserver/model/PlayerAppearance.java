package com.shnok.javaserver.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerAppearance {
    private byte face;
    private byte hairColor;
    private byte hairStyle;
    private boolean sex; // Female true(1)
    private boolean invisible = false;

    public PlayerAppearance(byte face, byte hairColor, byte hairStyle, boolean sex) {
        this.face = face;
        this.hairColor = hairColor;
        this.hairStyle = hairStyle;
        this.sex = sex;
    }
}

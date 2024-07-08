package com.shnok.javaserver.model.skills;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Skill {
    private String name;
    private float lvlBonusRate;
    private int magicLevel;
    private float castRange;
}

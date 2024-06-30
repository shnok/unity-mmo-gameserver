package com.shnok.javaserver.model.status;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
public class NpcStatus extends Status {
    public NpcStatus(int level, int maxHp) {
        super(level, maxHp);
    }
}

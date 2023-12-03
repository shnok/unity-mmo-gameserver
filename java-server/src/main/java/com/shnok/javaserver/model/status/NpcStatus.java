package com.shnok.javaserver.model.status;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NpcStatus extends Status {
    public NpcStatus(int level, int maxHp) {
        super(level, maxHp);
    }
}

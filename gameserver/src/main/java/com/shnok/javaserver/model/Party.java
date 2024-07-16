package com.shnok.javaserver.model;

import com.shnok.javaserver.model.object.entity.PlayerInstance;
import lombok.Getter;

import java.util.List;

@Getter
public class Party {
    private List<PlayerInstance> members;
}

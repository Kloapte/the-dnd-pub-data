package com.thedndpub.data.dte.monsters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MonstersWrapperDte {
    @JsonProperty("_meta")
    private Object meta;
    private List<MonsterDte> monster;
}

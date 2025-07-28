package com.thedndpub.data.dte.race;

import com.thedndpub.data.dte.race.deserializers.VariableDto;
import lombok.Data;

import java.util.List;

@Data
public class ImplementationDte {
    private VariableDto _variables;
    private List<String> resist;
}

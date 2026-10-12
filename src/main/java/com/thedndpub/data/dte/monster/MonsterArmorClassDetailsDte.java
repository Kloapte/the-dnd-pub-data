package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterArmorClassDetailsDte(Integer ac, List<String> from, String condition, Boolean braces,
                                          String special) implements MonsterArmorClassDte {
}

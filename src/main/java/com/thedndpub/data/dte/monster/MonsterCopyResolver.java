package com.thedndpub.data.dte.monster;

import java.util.List;

public interface MonsterCopyResolver {
    List<ResolvedMonster> resolveAll(List<MonsterSourceRecord> monsters);
}

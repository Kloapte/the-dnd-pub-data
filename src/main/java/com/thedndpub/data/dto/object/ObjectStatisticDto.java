package com.thedndpub.data.dto.object;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dte.object.ObjectNumericStatisticDte;
import com.thedndpub.data.dte.object.ObjectSpecialStatisticDte;
import com.thedndpub.data.dte.object.ObjectStatisticDte;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ObjectStatisticDto(Integer value, Boolean specialValuePresent) {
    public static ObjectStatisticDto from(ObjectStatisticDte statistic) {
        if (statistic instanceof ObjectNumericStatisticDte numeric) {
            return new ObjectStatisticDto(numeric.value(), null);
        }
        if (statistic instanceof ObjectSpecialStatisticDte) {
            return new ObjectStatisticDto(null, true);
        }
        return null;
    }
}

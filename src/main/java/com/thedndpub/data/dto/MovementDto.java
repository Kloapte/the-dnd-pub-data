package com.thedndpub.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MovementDto {
    private Integer walk;
    private boolean walkEstimated;
    private Integer fly;
    private boolean flyAvailable;
    private boolean flyEstimated;
}

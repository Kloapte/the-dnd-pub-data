package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubraceSourceDto extends SourceDto{
    private String name;

    public SubraceSourceDto(String name, SourceDto source) {
        this.setName(name);
        this.setSource(source.getSource());
        this.setOtherSources(source.getOtherSources());
        this.setEdition(source.getEdition());
        this.setPage(source.getPage());
        this.setDescription(source.getDescription());
        this.setLicense(source.getLicense());
        this.setReprintedAs(source.getReprintedAs());
    }
}

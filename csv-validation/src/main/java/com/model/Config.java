package com.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    @Getter
    @JsonProperty("fieldsList")
    List<Field> fieldsList;

}

package com.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Field implements Comparable<Field>{
    @UniqueElements
    @JsonProperty(value = "id", required = true)
    @Getter
    @NotEmpty
    @Min(0)
    Integer id;

    @Getter
    @JsonProperty(value = "description", required = true)
    @NotEmpty
    String description;

    @Getter
    @NotEmpty
    @JsonProperty(value = "canBeEmpty", defaultValue ="false", required = true)
    boolean canBeEmpty;

    @JsonProperty(value = "type", required = true)
    @Getter
    @NotEmpty
    String type;

    @JsonProperty("values")
    @Getter
    @NotEmpty
    List<String> values;


    @Override
    public int compareTo(Field o) {

        if (o.getId() > this.getId()) {
            return -1;
        } else if (o.getId() < this.getId()) {
            return 1;
        }

        return 0;

    }


}

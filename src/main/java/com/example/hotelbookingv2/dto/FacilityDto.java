package com.example.hotelbookingv2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter
@Getter
public class FacilityDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotNull
    @NotBlank(message = "Название не должно быть пустым")
    private String name;

    public FacilityDto(String id, String name) {
        this.id = id;
        this.name = name;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
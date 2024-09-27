package com.sky.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;

@Data
public class ShoppingCartDTO implements Serializable {

    private Long dishId;

    @JsonProperty("setmealId")
    private Long setMealId;

    private String dishFlavor;

}

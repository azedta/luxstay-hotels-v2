package com.luxstay.luxstay_hotels_v2.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RoomDtos {

    public record CreateRequest(
            @NotNull Long hotelId,
            @NotNull Integer roomNumber,
            @NotNull @DecimalMin("0.00") BigDecimal price,
            @NotNull @Min(1) Integer capacity,
            @NotNull Boolean extendable,
            String amenities,
            String problemsAndDamages,
            String imageUrl
    ) {}

    public record UpdateRequest(
            @NotNull Integer roomNumber,
            @NotNull @DecimalMin("0.00") BigDecimal price,
            @NotNull @Min(1) Integer capacity,
            @NotNull Boolean extendable,
            String amenities,
            String problemsAndDamages,
            String imageUrl
    ) {}

    public record Response(
            Long id,
            Long hotelId,
            String hotelName,
            String city,
            Long chainId,
            String chainName,
            Integer roomNumber,
            BigDecimal price,
            Integer capacity,
            Boolean extendable,
            String amenities,
            String problemsAndDamages,
            String imageUrl
    ) {}
}

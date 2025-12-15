package com.luxstay.luxstay_hotels_v2.web.dto;

import jakarta.validation.constraints.*;

public class HotelDtos {
    public record CreateRequest(
            @NotNull Long chainId,
            @NotBlank String name,
            @NotBlank String address,
            @NotBlank String city,
            String email,
            @Min(1) @Max(5) Integer rating
    ) {}

    public record UpdateRequest(
            @NotNull Long chainId,
            @NotBlank String name,
            @NotBlank String address,
            @NotBlank String city,
            String email,
            @Min(1) @Max(5) Integer rating
    ) {}

    public record Response(
            Long id,
            Long chainId,
            String chainName,
            String name,
            String address,
            String city,
            String email,
            Integer rating
    ) {}
}

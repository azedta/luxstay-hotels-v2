package com.luxstay.luxstay_hotels_v2.web.dto;

import com.luxstay.luxstay_hotels_v2.domain.enums.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationDtos {

    public record CreateRequest(
            @NotNull Long roomId,
            @NotNull Long customerId,
            Long handledByEmployeeId, // optional
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @NotNull ReservationType type
    ) {}

    public record Response(
            Long id,
            Long roomId,
            Long customerId,
            Long handledByEmployeeId,
            LocalDate startDate,
            LocalDate endDate,
            ReservationType type,
            ReservationStatus status,
            PaymentStatus paymentStatus,
            LocalDateTime checkedInAt,
            LocalDateTime checkedOutAt,
            String notes
    ) {}
}

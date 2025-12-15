package com.luxstay.luxstay_hotels_v2.web;

import com.luxstay.luxstay_hotels_v2.domain.Reservation;
import com.luxstay.luxstay_hotels_v2.domain.enums.ReservationStatus;
import com.luxstay.luxstay_hotels_v2.domain.enums.ReservationType;
import com.luxstay.luxstay_hotels_v2.domain.service.ReservationService;
import com.luxstay.luxstay_hotels_v2.web.dto.ReservationDtos;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/reservations")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReservationDtos.Response> list(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) ReservationType type
    ) {
        return service.list(customerId, roomId, status, type).stream().map(this::toDto).toList();
    }

    @PostMapping
    public ReservationDtos.Response create(@Valid @RequestBody ReservationDtos.CreateRequest req) {
        Reservation r = service.create(
                req.roomId(),
                req.customerId(),
                req.handledByEmployeeId(),
                req.startDate(),
                req.endDate(),
                req.type()
        );
        return toDto(r);
    }

    @PatchMapping("/{id}/cancel")
    public ReservationDtos.Response cancel(@PathVariable Long id) {
        return toDto(service.cancel(id));
    }

    @PatchMapping("/{id}/complete")
    public ReservationDtos.Response complete(@PathVariable Long id) {
        return toDto(service.complete(id));
    }

    @PatchMapping("/{id}/pay")
    public ReservationDtos.Response pay(@PathVariable Long id) {
        return toDto(service.pay(id));
    }

    @PatchMapping("/{id}/assign/{employeeId}")
    public ReservationDtos.Response assign(@PathVariable Long id, @PathVariable Long employeeId) {
        return toDto(service.assignEmployee(id, employeeId));
    }

    @PatchMapping("/{id}/check-in")
    public ReservationDtos.Response checkIn(@PathVariable Long id) {
        return toDto(service.checkIn(id));
    }

    @PatchMapping("/{id}/check-out")
    public ReservationDtos.Response checkOut(@PathVariable Long id) {
        return toDto(service.checkOut(id));
    }




    private ReservationDtos.Response toDto(Reservation r) {
        return new ReservationDtos.Response(
                r.getId(),
                r.getRoom().getId(),
                r.getCustomer().getId(),
                r.getHandledByEmployee() == null ? null : r.getHandledByEmployee().getId(),
                r.getStartDate(),
                r.getEndDate(),
                r.getType(),
                r.getStatus(),
                r.getPaymentStatus(),
                r.getCheckedInAt(),
                r.getCheckedOutAt(),
                r.getNotes()
        );
    }


}

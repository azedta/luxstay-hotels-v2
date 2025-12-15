package com.luxstay.luxstay_hotels_v2.web;

import com.luxstay.luxstay_hotels_v2.domain.Room;
import com.luxstay.luxstay_hotels_v2.domain.service.RoomService;
import com.luxstay.luxstay_hotels_v2.web.dto.RoomDtos;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/api/v2/rooms")
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    @GetMapping
    public List<RoomDtos.Response> list(
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String chainName
    ) {
        return service.list(hotelId, city, chainName).stream().map(this::toDto).toList();
    }

    @GetMapping("/{id}")
    public RoomDtos.Response get(@PathVariable Long id) {
        return toDto(service.get(id));
    }

    @PostMapping
    public RoomDtos.Response create(@Valid @RequestBody RoomDtos.CreateRequest req) {
        Room payload = Room.builder()
                .roomNumber(req.roomNumber())
                .price(req.price())
                .capacity(req.capacity())
                .extendable(req.extendable())
                .amenities(req.amenities())
                .problemsAndDamages(req.problemsAndDamages())
                .build();
        return toDto(service.create(req.hotelId(), payload));
    }

    @PutMapping("/{id}")
    public RoomDtos.Response update(@PathVariable Long id, @Valid @RequestBody RoomDtos.UpdateRequest req) {
        Room payload = Room.builder()
                .roomNumber(req.roomNumber())
                .price(req.price())
                .capacity(req.capacity())
                .extendable(req.extendable())
                .amenities(req.amenities())
                .problemsAndDamages(req.problemsAndDamages())
                .build();
        return toDto(service.update(id, payload));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/available")
    public List<RoomDtos.Response> available(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Long hotelId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String chainName,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return service.available(startDate, endDate, hotelId, city, chainName, capacity, maxPrice)
                .stream().map(this::toDto).toList();
    }

    private RoomDtos.Response toDto(Room r) {
        return new RoomDtos.Response(
                r.getId(),
                r.getHotel().getId(),
                r.getHotel().getName(),
                r.getHotel().getCity(),
                r.getHotel().getChain().getName(),
                r.getRoomNumber(),
                r.getPrice(),
                r.getCapacity(),
                r.getExtendable(),
                r.getAmenities(),
                r.getProblemsAndDamages()
        );
    }
}

package com.luxstay.luxstay_hotels_v2.domain.service;

import com.luxstay.luxstay_hotels_v2.domain.*;
import com.luxstay.luxstay_hotels_v2.domain.enums.*;
import com.luxstay.luxstay_hotels_v2.domain.repo.*;
import com.luxstay.luxstay_hotels_v2.web.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepo;
    private final RoomRepository roomRepo;
    private final CustomerRepository customerRepo;
    private final EmployeeRepository employeeRepo;

    public ReservationService(ReservationRepository reservationRepo,
                              RoomRepository roomRepo,
                              CustomerRepository customerRepo,
                              EmployeeRepository employeeRepo) {
        this.reservationRepo = reservationRepo;
        this.roomRepo = roomRepo;
        this.customerRepo = customerRepo;
        this.employeeRepo = employeeRepo;
    }

    public Reservation create(Long roomId,
                              Long customerId,
                              Long handledByEmployeeId,
                              LocalDate startDate,
                              LocalDate endDate,
                              ReservationType type) {

        if (startDate == null || endDate == null) throw new IllegalArgumentException("startDate/endDate required");
        if (!endDate.isAfter(startDate)) throw new IllegalArgumentException("endDate must be after startDate");

        // 🔒 LOCK the room row (FOR UPDATE) for the whole transaction
        Room room = roomRepo.findByIdForUpdate(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + roomId));

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));

        Employee emp = null;
        if (handledByEmployeeId != null) {
            emp = employeeRepo.findById(handledByEmployeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + handledByEmployeeId));
        }

        boolean hasConflicts = !reservationRepo.findActiveConflicts(
                roomId, ReservationStatus.ACTIVE, startDate, endDate
        ).isEmpty();

        if (hasConflicts) {
            // Better than IllegalArgumentException → we’ll map this to 409
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room is not available for the selected dates"
            );
        }

        Reservation r = Reservation.builder()
                .room(room)
                .customer(customer)
                .handledByEmployee(emp)
                .startDate(startDate)
                .endDate(endDate)
                .type(type)
                .status(ReservationStatus.ACTIVE)
                .paymentStatus(PaymentStatus.UNPAID)
                .build();

        return reservationRepo.save(r);
    }


    public List<Reservation> list(Long customerId, Long roomId, ReservationStatus status, ReservationType type) {
        return reservationRepo.search(customerId, roomId, status, type);
    }



    public Reservation cancel(Long id) {
        Reservation r = reservationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));
        r.setStatus(ReservationStatus.CANCELLED);
        return reservationRepo.save(r);
    }

    public Reservation complete(Long id) {
        Reservation r = reservationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));
        r.setStatus(ReservationStatus.COMPLETED);
        return reservationRepo.save(r);
    }

    public Reservation pay(Long id) {
        Reservation r = reservationRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + id));

        if (r.getStatus() == ReservationStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot pay a cancelled reservation");
        }

        if (r.getPaymentStatus() == PaymentStatus.PAID) {
            return r; // idempotent
        }

        r.setPaymentStatus(PaymentStatus.PAID);
        return reservationRepo.save(r);
    }

    public Reservation assignEmployee(Long reservationId, Long employeeId) {
        Reservation r = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));

        Employee e = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        r.setHandledByEmployee(e);
        return reservationRepo.save(r);
    }

    public Reservation checkIn(Long reservationId) {
        Reservation r = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));

        if (r.getStatus() != ReservationStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only ACTIVE reservations can be checked in");
        }

        if (r.getCheckedInAt() != null) {
            return r; // idempotent
        }

        // Optional strict rule: cannot check-in before startDate
        if (LocalDate.now().isBefore(r.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot check-in before reservation start date");
        }

        r.setCheckedInAt(LocalDateTime.now());
        return reservationRepo.save(r);
    }

    public Reservation checkOut(Long reservationId) {
        Reservation r = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found: " + reservationId));

        if (r.getStatus() != ReservationStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only ACTIVE reservations can be checked out");
        }

        if (r.getCheckedInAt() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot check-out before check-in");
        }

        if (r.getCheckedOutAt() != null) {
            return r; // idempotent
        }

        // Optional strict rule: cannot check-out before endDate
        // if (LocalDate.now().isBefore(r.getEndDate())) {
        //     throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot check-out before reservation end date");
        // }

        r.setCheckedOutAt(LocalDateTime.now());
        r.setStatus(ReservationStatus.COMPLETED); // check-out completes reservation
        return reservationRepo.save(r);
    }



}

package com.luxstay.luxstay_hotels_v2.domain.repo;

import com.luxstay.luxstay_hotels_v2.domain.Reservation;
import com.luxstay.luxstay_hotels_v2.domain.enums.ReservationStatus;
import com.luxstay.luxstay_hotels_v2.domain.enums.ReservationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Conflicts = ACTIVE reservations that overlap the requested interval:
    // existing.start < requestedEnd AND existing.end > requestedStart
    @Query("""
        select r
        from Reservation r
        where r.room.id = :roomId
          and r.status = :status
          and r.startDate < :endDate
          and r.endDate > :startDate
    """)
    List<Reservation> findActiveConflicts(
            @Param("roomId") Long roomId,
            @Param("status") ReservationStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        select r.room.id
        from Reservation r
        where r.status = :status
          and r.startDate < :endDate
          and r.endDate > :startDate
    """)
    List<Long> findBookedRoomIdsInRange(
            @Param("status") ReservationStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    select r
    from Reservation r
    where (:customerId is null or r.customer.id = :customerId)
      and (:roomId is null or r.room.id = :roomId)
      and (:status is null or r.status = :status)
      and (:type is null or r.type = :type)
    order by r.startDate desc
""")
    List<Reservation> search(
            @Param("customerId") Long customerId,
            @Param("roomId") Long roomId,
            @Param("status") ReservationStatus status,
            @Param("type") ReservationType type
    );

}

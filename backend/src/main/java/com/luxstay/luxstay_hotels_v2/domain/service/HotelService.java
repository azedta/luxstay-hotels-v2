package com.luxstay.luxstay_hotels_v2.domain.service;

import com.luxstay.luxstay_hotels_v2.domain.Hotel;
import com.luxstay.luxstay_hotels_v2.domain.HotelChain;
import com.luxstay.luxstay_hotels_v2.domain.repo.HotelChainRepository;
import com.luxstay.luxstay_hotels_v2.domain.repo.HotelRepository;
import com.luxstay.luxstay_hotels_v2.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HotelService {

    private final HotelRepository hotelRepo;
    private final HotelChainRepository chainRepo;

    public HotelService(HotelRepository hotelRepo, HotelChainRepository chainRepo) {
        this.hotelRepo = hotelRepo;
        this.chainRepo = chainRepo;
    }

    public List<Hotel> list(Long chainId, String city) {
        if (chainId != null) return hotelRepo.findByChainId(chainId);
        if (city != null && !city.isBlank()) return hotelRepo.findByCityIgnoreCase(city);
        return hotelRepo.findAll();
    }

    public Hotel get(Long id) {
        return hotelRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + id));
    }

    public Hotel create(Long chainId, Hotel payload) {
        HotelChain chain = chainRepo.findById(chainId)
                .orElseThrow(() -> new ResourceNotFoundException("HotelChain not found: " + chainId));
        payload.setId(null);
        payload.setChain(chain);
        return hotelRepo.save(payload);
    }

    public Hotel update(Long id, Hotel payload) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        hotel.setName(payload.getName());
        hotel.setAddress(payload.getAddress());
        hotel.setCity(payload.getCity());
        hotel.setEmail(payload.getEmail());
        hotel.setRating(payload.getRating());
        hotel.setImageUrl(payload.getImageUrl());

        return hotelRepo.save(hotel);
    }


    public void delete(Long id) {
        if (!hotelRepo.existsById(id)) throw new ResourceNotFoundException("Hotel not found: " + id);
        hotelRepo.deleteById(id);
    }
}

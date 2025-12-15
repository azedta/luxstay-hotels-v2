package com.luxstay.luxstay_hotels_v2.domain.service;

import com.luxstay.luxstay_hotels_v2.domain.HotelChain;
import com.luxstay.luxstay_hotels_v2.domain.repo.HotelChainRepository;
import com.luxstay.luxstay_hotels_v2.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HotelChainService {
    private final HotelChainRepository repo;

    public HotelChainService(HotelChainRepository repo) {
        this.repo = repo;
    }

    public List<HotelChain> list() {
        return repo.findAll();
    }

    public HotelChain get(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("HotelChain not found: " + id));
    }

    public HotelChain create(String name) {
        HotelChain chain = HotelChain.builder().name(name).build();
        return repo.save(chain);
    }

    public HotelChain update(Long id, String name) {
        HotelChain chain = get(id);
        chain.setName(name);
        return repo.save(chain);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("HotelChain not found: " + id);
        repo.deleteById(id);
    }
}

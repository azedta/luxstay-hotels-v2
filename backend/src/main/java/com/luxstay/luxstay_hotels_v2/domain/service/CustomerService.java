package com.luxstay.luxstay_hotels_v2.domain.service;

import com.luxstay.luxstay_hotels_v2.domain.Customer;
import com.luxstay.luxstay_hotels_v2.domain.enums.IdType;
import com.luxstay.luxstay_hotels_v2.domain.repo.CustomerRepository;
import com.luxstay.luxstay_hotels_v2.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    public List<Customer> list() {
        return repo.findAll();
    }

    public Customer get(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }

    public Customer create(String fullName,
                           String address,
                           LocalDate dateOfBirth,
                           String idNumber,
                           IdType idType) {

        repo.findByIdNumber(idNumber).ifPresent(c -> {
            throw new IllegalArgumentException("A customer with this idNumber already exists");
        });

        Customer c = Customer.builder()
                .id(null)
                .fullName(fullName)
                .address(address)
                .dateOfBirth(dateOfBirth)
                .idNumber(idNumber)
                .idType(idType)
                .registrationDate(LocalDate.now())
                .build();

        return repo.save(c);
    }

    public Customer update(Long id, String fullName, String address) {
        Customer existing = get(id);
        existing.setFullName(fullName);
        existing.setAddress(address);
        return repo.save(existing);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Customer not found: " + id);
        repo.deleteById(id);
    }
}

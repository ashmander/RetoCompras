package com.example.retocompras.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.retocompras.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}

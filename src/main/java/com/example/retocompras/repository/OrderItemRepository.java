package com.example.retocompras.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.retocompras.model.OrderItem;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

}

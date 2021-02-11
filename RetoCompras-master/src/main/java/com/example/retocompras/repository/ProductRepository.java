package com.example.retocompras.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.retocompras.model.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

	
}

package com.example.retocompras.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.retocompras.model.Invoice;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

}

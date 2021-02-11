package com.example.retocompras.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.retocompras.model.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {

	public Client findByIdentificationNumber(String identificationNumber);
}

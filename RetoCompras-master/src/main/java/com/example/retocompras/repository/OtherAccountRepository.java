package com.example.retocompras.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.retocompras.model.OtherAccount;

public interface OtherAccountRepository extends CrudRepository<OtherAccount, Long>{

	public OtherAccount findByConcept(String concept);
}

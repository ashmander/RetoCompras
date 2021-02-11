package com.example.retocompras.services;

import java.util.List;

import com.example.retocompras.model.Order;

public interface IOrderService {

	public Order createInvoice(Order invoice)  throws Exception;
	
	public boolean deleteInvoice(Long id);
	
	public Order updateInvoice(Order invoice) throws Exception;
	
	public List<Order> findAllOrders();
	
	public Order findById(Long id);
}

package com.example.retocompras.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.retocompras.model.Order;
import com.example.retocompras.services.IOrderService;

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
public class OrderController {

	@Autowired
	private IOrderService orderService;
	
	@GetMapping("/{id}")
	public Order findOrder(@PathVariable Long id) {
		return orderService.findById(id);
	}
	
	@GetMapping("/all")
	public List<Order> findAllOrders() {
		return orderService.findAllOrders();
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createOrder(@RequestBody Order order) {
		Order orderCreated = null;
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			orderCreated = orderService.createInvoice(order);
		} catch (Exception e) {
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		return new ResponseEntity<Order>(orderCreated, HttpStatus.CREATED);
	}
	
	@PutMapping("/update")
	public ResponseEntity<?> updateOrder(@RequestBody Order order) {
		Order orderUpdated = null;
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			orderUpdated = orderService.updateInvoice(order);
		} catch (Exception e) {
			response.put("error", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Order>(orderUpdated, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<String, Object>();
		if(orderService.deleteInvoice(id)) {
			response.put("message", "Se eliminó correctamente el pedido");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<Order>(orderService.findById(id), HttpStatus.OK);
		}
	}
}

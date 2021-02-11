package com.example.retocompras.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "invoices")
public class Invoice implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "total_to_pay")
	private Double totalToPay;
	
	private Double delivery;
	
	private Double iva;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getTotalToPay() {
		return totalToPay;
	}

	public void setTotalToPay(Double totalToPay) {
		this.totalToPay = totalToPay;
	}

	public Double getDelivery() {
		return delivery;
	}

	public void setDelivery(Double delivery) {
		this.delivery = delivery;
	}

	public Double getIva() {
		return iva;
	}

	public void setIva(Double iva) {
		this.iva = iva;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}

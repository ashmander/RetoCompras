package com.example.retocompras.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.retocompras.helper.Helper;
import com.example.retocompras.model.Client;
import com.example.retocompras.model.Invoice;
import com.example.retocompras.model.Order;
import com.example.retocompras.model.OrderItem;
import com.example.retocompras.model.OtherAccount;
import com.example.retocompras.model.Product;
import com.example.retocompras.repository.ClientRepository;
import com.example.retocompras.repository.InvoiceRepository;
import com.example.retocompras.repository.OrderItemRepository;
import com.example.retocompras.repository.OrderRepository;
import com.example.retocompras.repository.OtherAccountRepository;
import com.example.retocompras.repository.ProductRepository;

@Service
public class OrderService implements IOrderService {
	
	public static final Double MIN_DELIVERY = 70000.00;
	
	public static final Double MAX_DELIVERY = 100000.00;
	
	public static final Double DELETE_OUT_OF_TIME = 0.1;

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OtherAccountRepository otherAccountRepository;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	/**
	 * Método para crear el pedido
	 */
	@Override
	public Order createInvoice(Order order) throws Exception {
		Double totalInvoice = 0.0;
		Client client = clientRepository.findById(order.getClient().getId()).orElse(null);
		if(order.getClient().getId() != null) {
			client = clientRepository.findById(order.getClient().getId()).orElse(null);
		} else if(order.getClient().getIdentificationNumber() != null) {
			client = clientRepository.findById(order.getClient().getId()).orElse(null);
		} else {
			throw new Exception("No ha proporcionado los datos del cliente");
		}
		//Se calcula el total de cada item y se va sumando para obtener el total del pedido
		for(int i = 0; i < order.getItems().size(); i++) {
			Product product = productRepository.findById(order.getItems().get(i).getProduct().getId()).orElse(null);
			if(product != null) {
				Double totalItem = order.getItems().get(i).getQuantity() * product.getPrice();
				order.getItems().get(i).setTotal(totalItem);
				order.getItems().get(i).setProduct(product);
				totalInvoice += totalItem;			
			}
		}
		order.setTotal(totalInvoice);
		//Otras cuentas: IVA y DOMICILIO, se verifica si aplica o no
		List<OtherAccount> otherAccounts = new ArrayList<OtherAccount>();
		OtherAccount iva = otherAccountRepository.findByConcept("IVA");
		OtherAccount delivery = otherAccountRepository.findByConcept("DOMICILIO");
		Invoice invoice = new Invoice();
		otherAccounts.add(iva);
		invoice.setIva(iva.getValue());
		if(totalInvoice >= MIN_DELIVERY && totalInvoice < MAX_DELIVERY) {
			totalInvoice += totalInvoice * (iva.getValue() / 100.0);
			totalInvoice += delivery.getValue();
			invoice.setDelivery(delivery.getValue());
			otherAccounts.add(delivery);
		} else if(totalInvoice >= MAX_DELIVERY) {
			totalInvoice += totalInvoice * (iva.getValue() / 100.0);
			invoice.setDelivery(0.0);
			otherAccounts.add(delivery);
		} else {
			totalInvoice += totalInvoice * (iva.getValue() / 100.0);
		}
		order.setOtherAccounts(otherAccounts);
		order.setInvoice(invoice);
		order.setState("REALIZADA");
		System.out.print(Calendar.getInstance().getTime());
		order.setCreatedDate(Calendar.getInstance().getTime());
		order.setClient(client);
		invoice.setTotalToPay(totalInvoice);
		invoice.setOrder(order);
		return orderRepository.save(order);
	}

	/**
	 * Método para eliminar un pedido
	 */
	@Override
	public boolean deleteInvoice(Long id) {
		Order orderToDelete = orderRepository.findById(id).orElse(null);
		//Se verifica si no han transcurrido más de 12 horas
		if(isUpdatable(orderToDelete, 12)) {
			orderRepository.deleteById(id);
			return true;
		} else {
			//Se genera una nueva factura cobrando el 10% del pedido, el pedido se guarda con estado CANCELADO
			invoiceRepository.deleteById(orderToDelete.getInvoice().getId());
			Invoice invoice = invoiceRepository.findById(orderToDelete.getInvoice().getId()).orElse(null);
			orderToDelete.setState("CANCELADO");
			orderToDelete.setInvoice(invoice);
			Double totalToPay = invoice.getTotalToPay() * DELETE_OUT_OF_TIME;
			invoice.setTotalToPay(totalToPay);
			invoice.setOrder(orderToDelete);
			orderRepository.save(orderToDelete);
			return false;
		}
	}

	/**
	 * Método para actualizar el pedido
	 */
	@Override
	public Order updateInvoice(Order order) throws Exception {
		List<OrderItem> orderItemsToSave = new ArrayList<OrderItem>();
		Client client = null;
		if(order.getClient().getId() != null) {
			client = clientRepository.findById(order.getClient().getId()).orElse(null);
		} else {
			client = clientRepository.findByIdentificationNumber(order.getClient().getIdentificationNumber());
		}
		if(order.getId() != null) {
			Order orderCreated = orderRepository.findById(order.getId()).orElse(null);
			Double totalOrder = 0.0;
			if(orderCreated != null) {
				//Se verifica si no han pasado más de 5 horas
				if(isUpdatable(orderCreated, 5)) {
					for(int i = 0; i < order.getItems().size(); i++) {
						//Si es un item nuevo, se verifica que el precio de ese producto sea mayor o igual al de los anteriores productos
						if(order.getItems().get(i).getId() == null) {
							Product product = productRepository.findById(order.getItems().get(i).getProduct().getId()).orElse(null);
							if(verifyProductPrice(product, orderCreated)) {
								if(product != null) {
									Double totalItem = order.getItems().get(i).getQuantity() * product.getPrice();
									order.getItems().get(i).setTotal(totalItem);
									order.getItems().get(i).setProduct(product);
									totalOrder += totalItem;			
								}								
							} else {
								throw new Exception("Uno de los productos que desea agregar tiene un precio menor a los que agregó la primera vez que creó el pedido");
							}
						} else {
							OrderItem orderItemAux = orderItemRepository.findById(order.getItems().get(i).getId()).orElse(null);
							//Se verifica si el id del item existe
							if(orderItemAux != null) {
								//Si no es un producto nuevo se actualiza
								Product product = productRepository.findById(order.getItems().get(i).getProduct().getId()).orElse(null);
								Double totalItem = order.getItems().get(i).getQuantity() * product.getPrice();
								order.getItems().get(i).setTotal(totalItem);
								order.getItems().get(i).setProduct(product);
								totalOrder += totalItem;
								//Se verifica que id se actualizará y se añade a los items para guardar
								for(int j = 0; j < orderCreated.getItems().size(); j++) {
									if(orderCreated.getId().compareTo(order.getItems().get(i).getId()) == 0) {
										orderItemsToSave.add(orderCreated.getItems().get(j));
									}
								}								
							} else {
								throw new Exception("El item que se quiere actualizar tiene un id que no existe");
							}
						}
					}
				} else {
					throw new Exception("El pedido no se puede actualizar porque ya han pasado más de 5 horas");
				}
			}
			order.setOtherAccounts(orderCreated.getOtherAccounts());
			//Se crea la nueva factura
			OtherAccount iva = otherAccountRepository.findByConcept("IVA");
			OtherAccount delivery = otherAccountRepository.findByConcept("DOMICILIO");
			if(orderCreated.getTotal() < MIN_DELIVERY && totalOrder >= MIN_DELIVERY) {
				order.getOtherAccounts().add(delivery);
			}
			order.setTotal(totalOrder);
			Invoice invoice = invoiceRepository.findById(orderCreated.getInvoice().getId()).orElse(null);
			if(totalOrder >= MIN_DELIVERY && totalOrder <= MAX_DELIVERY) {
				totalOrder += totalOrder * (iva.getValue() / 100.0);
				totalOrder += delivery.getValue();
				invoice.setIva(iva.getValue());
				invoice.setDelivery(delivery.getValue());
			} else if(totalOrder >= MAX_DELIVERY) {
				totalOrder += totalOrder * (iva.getValue() / 100.0);
				invoice.setIva(iva.getValue());
				invoice.setDelivery(0.0);
			} else {
				totalOrder += totalOrder * (iva.getValue() / 100.0);
				invoice.setIva(iva.getValue());
			}
			deleteItems(orderItemsToSave, orderCreated.getItems());
			order.setInvoice(invoice);
			order.setState("REALIZADA");
			order.setCreatedDate(Calendar.getInstance().getTime());
			order.setClient(client);
			invoice.setTotalToPay(totalOrder);
			invoice.setOrder(order);
			return orderRepository.save(order);
		}
		return null;
	}
	
	/**
	 * Método para borrar los items que fueron removidos
	 * @param orderItemsToSave items que se deben mantener
	 * @param orderItemsToVerify todos los items que fueron guardados inicialmente
	 */
	private void deleteItems(List<OrderItem> orderItemsToSave, List<OrderItem> orderItemsToVerify) {
		boolean save = false;
		for(int i = 0; i < orderItemsToVerify.size(); i++) {
			for(int j = 0; j < orderItemsToSave.size(); j++) {
				//Se verifica cual item se debe mantener
				if(orderItemsToVerify.get(i).getId().compareTo(orderItemsToSave.get(j).getId()) == 0) {
					save = true;
				}
			}
			//Si este item no se debe mantener, se elimina
			if(!save) {
				orderItemRepository.deleteById(orderItemsToVerify.get(i).getId());
			}
		}
	}
	
	/**
	 * Método para verificar el precio del producto que se quiere añadir a un pedido
	 * @param product producto que se quiere añadir
	 * @param order pedido con los productos que se pidieron inicialmente
	 * @return true si el precio del producto que se quiere añadir no es menor que al precio de los productos que ya habían
	 */
	private boolean verifyProductPrice(Product product, Order order) {
		for(OrderItem item : order.getItems()) {
			if(item.getProduct().getPrice() > product.getPrice()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Método que verifica si el producto es editable ya sea si se quiere editar o eliminar
	 * @param order pedido que se quiere verificar
	 * @param hours horas que no deben haber transcurrido desde la creación del pedido
	 * @return true si se puede editar, false si no se puede editar
	 */
	private boolean isUpdatable(Order order, int hours) {
		Calendar initialDate = Calendar.getInstance();
		initialDate.setTime(order.getCreatedDate());
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(Calendar.getInstance().getTime());
		int initialHour = initialDate.get(Calendar.HOUR_OF_DAY);
		int initialMinute = initialDate.get(Calendar.MINUTE);
		int endHour = endDate.get(Calendar.HOUR_OF_DAY);
		int endMinute = endDate.get(Calendar.MINUTE);
		if(Helper.getHour(initialHour + hours) < initialHour) {
			if(initialDate.get(Calendar.DAY_OF_YEAR) == endDate.get(Calendar.DAY_OF_YEAR)) {
				return true;
			}
			initialDate.add(Calendar.DAY_OF_YEAR, 1);
			if(initialDate.get(Calendar.DAY_OF_YEAR) == endDate.get(Calendar.DAY_OF_YEAR)) {
				if(Helper.getHour(initialHour + hours) == endHour) {
					if(initialMinute >= endMinute) {
						return true;						
					}
				}
				if(Helper.getHour(initialHour + hours) > endHour) {
					return true;
				}
			}
		} else {
			if(initialDate.get(Calendar.DAY_OF_YEAR) ==  endDate.get(Calendar.DAY_OF_YEAR)) {
				if((initialHour + hours) == endHour) {
					if(initialMinute >= endMinute) {
						return true;						
					}
				}
				if((initialHour + hours) > endHour) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Método para listar todos los pedidos realizados hasta el momento
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Order> findAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public Order findById(Long id) {
		return orderRepository.findById(id).orElse(null);
	}

}

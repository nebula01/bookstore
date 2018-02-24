package com.bookstore.service.impl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.domain.BillingAddress;
import com.bookstore.domain.Book;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.Order;
import com.bookstore.domain.Payment;
import com.bookstore.domain.ShippingAddress;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;
import com.bookstore.repository.OrderRepository;
import com.bookstore.service.CartItemService;
import com.bookstore.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository; 
	
	@Autowired
	private CartItemService cartItemService;
	
	@Override
	public synchronized Order createOrder(ShoppingCart shoppingCart, ShippingAddress shippingAddress, BillingAddress billingAddress,
			Payment payment, String shippingMethod, User user) {
		// TODO Auto-generated method stub
		Order order = new Order();
		order.setOrderStatus("created");
		order.setPayment(payment);
		order.setBillingAddress(billingAddress);
		order.setShippingAddress(shippingAddress);
		order.setShippingMethod(shippingMethod);
		
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
		
		for (CartItem cartItem : cartItemList) {
			Book book = cartItem.getBook();
			cartItem.setOrder(order);
			// 현재 재고수에서 주문량 만큼 빼주기(계산이 있기 때문에 메서드에 sync붙여줌)
			book.setInStockNumber(book.getInStockNumber() - cartItem.getQty());
		}
		
		order.setCartItemList(cartItemList);
		order.setOrderDate(Calendar.getInstance().getTime());
		order.setOrderTotal(shoppingCart.getGrandTotal());
		shippingAddress.setOrder(order);
		billingAddress.setOrder(order);
		payment.setOrder(order);
		order.setUser(user);
		
		// 디비에 저장
		order = orderRepository.save(order);
		
		return order;
	}

	@Override
	public Order findOne(Long id) {
		// TODO Auto-generated method stub
		return orderRepository.findOne(id);
	}

}

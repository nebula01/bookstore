package com.bookstore.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bookstore.domain.CartItem;
import com.bookstore.domain.ShoppingCart;

public interface CartItemRepository extends CrudRepository<CartItem, Long>{

	// 장바구니로 책들 찾기
	List<CartItem> findByShoppingCart(ShoppingCart shoppingCart);
}

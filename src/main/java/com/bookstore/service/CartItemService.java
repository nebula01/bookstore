package com.bookstore.service;

import java.util.List;

import com.bookstore.domain.CartItem;
import com.bookstore.domain.ShoppingCart;

public interface CartItemService {
	
	// 장바구니에 담긴 책들 찾기
	List<CartItem> findByShoppingCart(ShoppingCart shoppingCart);
	
	// 장바구니에서 책 아이템 수정
	CartItem updateCartItem(CartItem cartItem);
}

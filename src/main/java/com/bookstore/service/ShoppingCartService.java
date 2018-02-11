package com.bookstore.service;

import com.bookstore.domain.ShoppingCart;

public interface ShoppingCartService {

	// 장바구니 내용 수정
	ShoppingCart updateShoppingCart(ShoppingCart shoppingCart);
}

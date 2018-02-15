package com.bookstore.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.domain.Book;
import com.bookstore.domain.BookToCartItem;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;
import com.bookstore.repository.BookToCartItemRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.service.CartItemService;

@Service
public class CartItemServiceImpl implements CartItemService {

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private BookToCartItemRepository bookToCartItemRepository;
 	
	@Override
	public List<CartItem> findByShoppingCart(ShoppingCart shoppingCart) {
		// TODO Auto-generated method stub
		return cartItemRepository.findByShoppingCart(shoppingCart);
	}

	@Override
	public CartItem updateCartItem(CartItem cartItem) {
		// TODO Auto-generated method stub
		
		BigDecimal bigDecimal = new BigDecimal(cartItem.getBook().getOurPrice())
				.multiply(new BigDecimal(cartItem.getQty()));
		
		bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
		cartItem.setSubtotal(bigDecimal);
		
		cartItemRepository.save(cartItem);
		
		return cartItem;
	}
	
	@Override
	public CartItem addBookToCartItem(Book book, User user, int qty) {
		// TODO Auto-generated method stub
		
		List<CartItem> cartItemList = findByShoppingCart(user.getShoppingCart());
		
		for(CartItem cartItem : cartItemList) {
			if(book.getId() == cartItem.getBook().getId()) {
				// 이미 등록되어 있는 책 id와 수정하려는 책 id가 같을 경우 권 수 추가
				cartItem.setQty(cartItem.getQty() + qty);
				cartItem.setSubtotal(new BigDecimal(book.getOurPrice()).multiply(new BigDecimal(qty)));
				// 디비에 저장
				cartItemRepository.save(cartItem);
				return cartItem;
			}
		}
		
		CartItem cartItem = new CartItem();
		cartItem.setShoppingCart(user.getShoppingCart());
		cartItem.setBook(book);
		cartItem.setQty(qty);
		cartItem.setSubtotal(new BigDecimal(book.getOurPrice()).multiply(new BigDecimal(qty)));
		// 디비에 저장	
		cartItem = cartItemRepository.save(cartItem);
		
		BookToCartItem bookToCartItem = new BookToCartItem();
		bookToCartItem.setCartItem(cartItem);
		bookToCartItem.setBook(book);
		// 디비에 저장
		bookToCartItemRepository.save(bookToCartItem);
		
		return cartItem;
	}
	
	public CartItem findById(Long id) {
		return cartItemRepository.findOne(id);
	}
	
	@Override
	public void removeCartItem(CartItem cartItem) {
		// TODO Auto-generated method stub
		bookToCartItemRepository.deleteByCartItem(cartItem);
		cartItemRepository.delete(cartItem);
	}
}

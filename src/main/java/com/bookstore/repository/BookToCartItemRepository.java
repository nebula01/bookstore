package com.bookstore.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.domain.BookToCartItem;
import com.bookstore.domain.CartItem;

@Transactional // 카트 아이템 삭제시  다음의 db작업과 트랜잭션 문제 생김
public interface BookToCartItemRepository extends CrudRepository<BookToCartItem, Long>{
	
	void deleteByCartItem(CartItem cartItem);
}

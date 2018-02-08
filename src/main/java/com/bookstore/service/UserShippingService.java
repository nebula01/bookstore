package com.bookstore.service;

import com.bookstore.domain.UserShipping;

public interface UserShippingService {

	// id로 배송지 주소 찾기	
	UserShipping findById(Long id);

	// id로 배송지 삭제
	void removeById(Long id);
}

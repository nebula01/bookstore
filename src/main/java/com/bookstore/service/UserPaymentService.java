package com.bookstore.service;

import com.bookstore.domain.UserPayment;

public interface UserPaymentService {
	
	//id로 찾기
	UserPayment findById(Long id);
	
	//id로 삭제
	void removeById(Long id);
}

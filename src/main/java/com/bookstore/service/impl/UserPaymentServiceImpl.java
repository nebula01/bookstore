package com.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.domain.UserPayment;
import com.bookstore.repository.UserPaymentRepository;
import com.bookstore.service.UserPaymentService;

@Service
public class UserPaymentServiceImpl implements UserPaymentService {

	@Autowired
	private UserPaymentRepository userPaymentRepository;
	
	// 아이디로 구매 정보 찾기
	@Override
	public UserPayment findById(Long id) {
		// TODO Auto-generated method stub
		return userPaymentRepository.findOne(id);
	}

}

package com.bookstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookstore.domain.UserShipping;
import com.bookstore.repository.UserShippingRepository;
import com.bookstore.service.UserShippingService;

@Service
public class UserShippingServiceImpl implements UserShippingService {

	@Autowired
	private UserShippingRepository userShippingRepository;
	
	// id로 배송정보 찾기
	@Override
	public UserShipping findById(Long id) {
		// TODO Auto-generated method stub
		return userShippingRepository.findOne(id);
	}

	// id로 삭제
	@Override
	public void removeById(Long id) {
		// TODO Auto-generated method stub
		userShippingRepository.delete(id);
	}
}

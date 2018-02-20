package com.bookstore.service.impl;

import org.springframework.stereotype.Service;

import com.bookstore.domain.BillingAddress;
import com.bookstore.domain.UserBilling;
import com.bookstore.service.BillingAddressService;

@Service
public class BillingAddressServiceImpl implements BillingAddressService {

	@Override
	public BillingAddress setByUserBilling(UserBilling userBilling, BillingAddress billingAddress) {
		
		billingAddress.setBillingAddressName(userBilling.getUserBillingName());
		billingAddress.setBillingAddressStreet(userBilling.getUserBillingStreet());
		billingAddress.setBillingAddressCity(userBilling.getUserBillingCity());
		billingAddress.setBillingAddressState(userBilling.getUserBillingState());
		billingAddress.setBillingAddressCountry(userBilling.getUserBillingCountry());
		billingAddress.setBillingAddressZipcode(userBilling.getUserBillingZipcode());
		
		return billingAddress;
	}

}

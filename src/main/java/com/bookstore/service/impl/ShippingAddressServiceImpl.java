package com.bookstore.service.impl;

import com.bookstore.domain.ShippingAddress;
import com.bookstore.domain.UserShipping;
import com.bookstore.service.ShippingAddressService;

public class ShippingAddressServiceImpl implements ShippingAddressService {

	@Override
	public ShippingAddress setByUserShipping(UserShipping userShipping, ShippingAddress shippingAddress) {
		// TODO Auto-generated method stub
		shippingAddress.setshippingAddressName(userShipping.getUserShippingName());
		shippingAddress.setshippingAddressStreet(userShipping.getUserShippingStreet());
		shippingAddress.setshippingAddressCity(userShipping.getUserShippingCity());
		shippingAddress.setshippingAddressState(userShipping.getUserShippingState());
		shippingAddress.setshippingAddressCountry(userShipping.getUserShippingCountry());
		shippingAddress.setshippingAddressZipcode(userShipping.getUserShippingZipcode());
		
		return shippingAddress;
	}

}

package com.bookstore.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;
import com.bookstore.domain.UserBilling;
import com.bookstore.domain.UserPayment;
import com.bookstore.domain.UserShipping;
import com.bookstore.domain.security.PasswordResetToken;
import com.bookstore.domain.security.UserRole;
import com.bookstore.repository.PasswordResetTokenRepository;
import com.bookstore.repository.RoleRepository;
import com.bookstore.repository.UserPaymentRepository;
import com.bookstore.repository.UserRepository;
import com.bookstore.repository.UserShippingRepository;
import com.bookstore.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	private UserPaymentRepository userPaymentRepository;

	@Autowired
	private UserShippingRepository userShippingRepository;
	
	@Override
	public PasswordResetToken getPasswordResetToken(final String token) {
		return passwordResetTokenRepository.findByToken(token);
	}

	@Override
	public void createPasswordResetTokenForUser(final User user, final String token) {
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordResetTokenRepository.save(myToken);
	}

	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	@Transactional
	public User createUser(User user, Set<UserRole> userRoles) {
		User localUser = userRepository.findByUsername(user.getUsername());

		if (localUser != null) {
			LOG.info("user {} already exists. Nothing will be done.", user.getUsername());
		} else {
			for (UserRole ur : userRoles) {
				roleRepository.save(ur.getRole());
			}

			user.getUserRoles().addAll(userRoles);

			ShoppingCart shoppingCart = new ShoppingCart();
			
			shoppingCart.setUser(user);
			
			user.setShoppingCart(shoppingCart);
			
			user.setUserShippingList(new ArrayList<UserShipping>());
			user.setUserPaymentList(new ArrayList<UserPayment>());
			
			localUser = userRepository.save(user);
		}

		return localUser;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}

	@Override
	public void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user) {
		// TODO Auto-generated method stub
		userPayment.setUser(user);
		userPayment.setUserBilling(userBilling);
		userPayment.setDefaultPayment(true);

		userBilling.setUserPayment(userPayment);
		user.getUserPaymentList().add(userPayment);

		// 디비에 저장
		save(user);

	}

	// 기본 결제 정보 등록
	@Override
	public void setUserDefaultPayment(Long id, User user) {

		// 사용자의 모든 구매 수단을 찾음
		List<UserPayment> userPaymentList = (List<UserPayment>) userPaymentRepository.findAll();

		for (UserPayment userPayment : userPaymentList) {
			if (userPayment.getId() == id) { // 구매수단 정보 중 현재 선택한 id와 같을 경우에만
				// 사용자 구매정보 수단중 setDefaultPayment 속성 true
				// System.out.println(userPayment.getId());
				// System.out.println(id);
				userPayment.setDefaultPayment(true);

			} else { // 그 밖에는 false
				userPayment.setDefaultPayment(false);
			}
			// 디비에 저장 (위치 주의)
			userPaymentRepository.save(userPayment);
		}
	}

	@Override
	public void updateUserShipping(UserShipping userShipping, User user) {
		// TODO Auto-generated method stub
		userShipping.setUser(user);
		userShipping.setUserShippingDefault(true);
		user.getUserShippingList().add(userShipping);

		// 디비에 저장
		save(user);
	}

	@Override
	public void setUserDefaultShipping(Long id, User user) {
		// TODO Auto-generated method stub
		// 사용자의 모든 배송지 정보를 찾음
		List<UserShipping> userShippingList = (List<UserShipping>) userShippingRepository.findAll();

		for (UserShipping userShipping : userShippingList) {
			if (userShipping.getId() == id) { // 구매수단 정보 중 현재 선택한 id와 같을 경우에만
				userShipping.setUserShippingDefault(true);

			} else { // 그 밖에는 false
				userShipping.setUserShippingDefault(false);
			}
			// 디비에 저장 (위치 주의)
			userShippingRepository.save(userShipping);
		}
	}
}

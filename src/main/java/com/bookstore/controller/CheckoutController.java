package com.bookstore.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.domain.BillingAddress;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.Order;
import com.bookstore.domain.Payment;
import com.bookstore.domain.ShippingAddress;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;
import com.bookstore.domain.UserBilling;
import com.bookstore.domain.UserPayment;
import com.bookstore.domain.UserShipping;
import com.bookstore.service.BillingAddressService;
import com.bookstore.service.CartItemService;
import com.bookstore.service.PaymentService;
import com.bookstore.service.ShippingAddressService;
import com.bookstore.service.ShoppingCartService;
import com.bookstore.service.UserPaymentService;
import com.bookstore.service.UserService;
import com.bookstore.service.UserShippingService;
import com.bookstore.utility.KRConstants;
//import com.sun.scenario.effect.Blend.Mode;

@Controller
public class CheckoutController {

	// @entity는 @autowired를 통해 자동 생성 안됨
	private ShippingAddress shippingAddress = new ShippingAddress();
	private BillingAddress billingAddress = new BillingAddress();
	private Payment payment = new Payment();

	@Autowired
	private UserService userService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private ShippingAddressService shippingAddressService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private BillingAddressService billingAddressService;

	@Autowired
	private UserShippingService userShippingService;

	@Autowired
	private UserPaymentService userPaymentService;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private ShoppingCartService shoppingCartService;
	
	@RequestMapping("/checkout")
	public String checkout(@RequestParam("id") Long id,
			@RequestParam(value = "missingRequiredField", required = false) boolean missingRequiredField, Model model,
			Principal principal) {

		// 로그인한 유저
		User user = userService.findByUsername(principal.getName());

		if (id != user.getShoppingCart().getId()) {
			return "badRequestPage";
		}

		List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

		if (cartItemList.size() == 0) {
			model.addAttribute("emptyCart", true);
			return "forward:/shoppingCart/cart";
		}

		for (CartItem cartItem : cartItemList) {
			if (cartItem.getBook().getInStockNumber() < cartItem.getQty()) {
				model.addAttribute("notEnoughStock", true);
				return "forward:/shoppingCart/cart";
			}
		}

		List<UserShipping> userShippingList = user.getUserShippingList();
		List<UserPayment> userPaymentList = user.getUserPaymentList();

		model.addAttribute("userShippingList", userShippingList);
		model.addAttribute("userPaymentList", userPaymentList);

		if (userPaymentList.size() == 0) {
			model.addAttribute("emptyPaymentList", true);
		} else {
			model.addAttribute("emptyPaymentList", false);
		}

		if (userShippingList.size() == 0) {
			model.addAttribute("emptyShippingList", true);
		} else {
			model.addAttribute("emptyShippingList", false);
		}

		ShoppingCart shoppingCart = user.getShoppingCart();

		for (UserShipping userShipping : userShippingList) {
			if (userShipping.isUserShippingDefault()) {
				shippingAddressService.setByUserShipping(userShipping, shippingAddress);
			}
		}

		for (UserPayment userPayment : userPaymentList) {
			if (userPayment.isDefaultPayment()) {
				paymentService.setByUserPayment(userPayment, payment);
				billingAddressService.setByUserBilling(userPayment.getUserBilling(), billingAddress);
			}
		}

		model.addAttribute("shippingAddress", shippingAddress);
		System.out.println(shippingAddress.getShippingAddressName());
		model.addAttribute("payment", payment);
		model.addAttribute("billingAddress", billingAddress);
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", user.getShoppingCart());

		List<String> stateList = KRConstants.listOfKRStatesCode;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);

		model.addAttribute("classActiveShipping", true);

		if (missingRequiredField) {
			model.addAttribute("missingRequiredField", true);
		}

		return "checkout";
	}

	// 자주 쓰는 배송지 주소
	@RequestMapping("/setShippingAddress")
	public String setShippingAddress(@RequestParam("userShippingId") Long id, Principal principal, Model model) {

		User user = userService.findByUsername(principal.getName());

		UserShipping userShipping = userShippingService.findById(id);

		if (userShipping.getUser().getId() != user.getId()) {
			return "badRequestPage";
		} else {
			shippingAddressService.setByUserShipping(userShipping, shippingAddress);

			List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

			model.addAttribute("shippingAddress", shippingAddress);
			System.out.println(shippingAddress.getShippingAddressName());
			model.addAttribute("payment", payment);
			model.addAttribute("billingAddress", billingAddress);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("shoppingCart", user.getShoppingCart());

			List<String> stateList = KRConstants.listOfKRStatesCode;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);

			List<UserShipping> userShippingList = user.getUserShippingList();
			List<UserPayment> userPaymentList = user.getUserPaymentList();

			model.addAttribute("userShippingList", userShippingList);
			model.addAttribute("userPaymentList", userPaymentList);

			model.addAttribute("shippingAddress", shippingAddress);

			model.addAttribute("classActiveShipping", true);

			if (userPaymentList.size() == 0) {
				model.addAttribute("emptyPaymentList", true);
			} else {
				model.addAttribute("emptyPaymentList", false);
			}

			model.addAttribute("emptyShippingList", false);

			return "checkout";
		}
	}

	// 자주 쓰는 카드 등록
	@RequestMapping("/setPaymentMethod")
	public String setPaymentMethod(@RequestParam("userPaymentId") Long id, Principal principal, Model model) {

		// 로그인한 유저 찾음
		User user = userService.findByUsername(principal.getName());

		// 유저의 구매정보를 찾음
		UserPayment userPayment = userPaymentService.findById(id);

		// 유저 구매정보에서 청구지 정보를 찾음
		UserBilling userBilling = userPayment.getUserBilling();

		if (userPayment.getUser().getId() != user.getId()) {
			return "badRequestPage";
		} else {
			paymentService.setByUserPayment(userPayment, payment);

			List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

			billingAddressService.setByUserBilling(userBilling, billingAddress);

			model.addAttribute("shippingAddress", shippingAddress);
			model.addAttribute("payment", payment);
			model.addAttribute("billingAddress", billingAddress);
			model.addAttribute("cartItemList", cartItemList);
			model.addAttribute("shoppingCart", user.getShoppingCart());

			List<String> stateList = KRConstants.listOfKRStatesCode;
			Collections.sort(stateList);
			model.addAttribute("stateList", stateList);

			List<UserShipping> userShippingList = user.getUserShippingList();
			List<UserPayment> userPaymentList = user.getUserPaymentList();

			model.addAttribute("userShippingList", userShippingList);
			model.addAttribute("userPaymentList", userPaymentList);

			model.addAttribute("shippingAddress", shippingAddress);

			// 템플릿의 구매 부분 보여주기
			model.addAttribute("classActivePayment", true);
			
			// 구매정보이기 때문에 배송정보가 없을 경우 보여주지 않음 
			if (userShippingList.size() == 0) {
				model.addAttribute("emptyShippingList", true);
			} else {
				model.addAttribute("emptyShippingList", false);
			}

			model.addAttribute("emptyPaymentList", false);

			return "checkout";
		}
	}
	
	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	public String postCheckout(
			@ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
			@ModelAttribute("billingAddress") BillingAddress billingAddress,
			@ModelAttribute("payment") Payment payment,
			@ModelAttribute("billingSameAsShipping") String billingSameAsShipping,
			@ModelAttribute("shippingMethod") String shippingMethod,
			Principal principal, Model model) {
		
			// 로그인한 유저에서 shoppingCart 객체 반환
			ShoppingCart shoppingCart = userService.findByUsername(principal.getName()).getShoppingCart();
			
			List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);
			
			model.addAttribute("cartItemList", cartItemList);
			
			// 배송지 주소와 같게가 체크 되어 있을 경우의 처리
			if (billingSameAsShipping.equals("true")) {
				billingAddress.setBillingAddressName(shippingAddress.getShippingAddressName());
				billingAddress.setBillingAddressStreet(shippingAddress.getShippingAddressStreet());
				billingAddress.setBillingAddressCity(shippingAddress.getShippingAddressCity());
				billingAddress.setBillingAddressState(shippingAddress.getShippingAddressState());
				billingAddress.setBillingAddressCountry(shippingAddress.getShippingAddressCountry());
				billingAddress.setBillingAddressZipcode(shippingAddress.getShippingAddressZipcode());
			}
			
			// 항목이 비어 있을 때 처리
			if (shippingAddress.getShippingAddressStreet().isEmpty() || shippingAddress.getShippingAddressCity().isEmpty()
					|| shippingAddress.getShippingAddressState().isEmpty()
					|| shippingAddress.getShippingAddressName().isEmpty()
					|| shippingAddress.getShippingAddressZipcode().isEmpty() || payment.getCardNumber().isEmpty()
					|| payment.getCvc() == 0 || billingAddress.getBillingAddressStreet().isEmpty()
					|| billingAddress.getBillingAddressCity().isEmpty() || billingAddress.getBillingAddressState().isEmpty()
					|| billingAddress.getBillingAddressName().isEmpty()
					|| billingAddress.getBillingAddressZipcode().isEmpty())
				return "redirect:/checkout?id=" + shoppingCart.getId() + "&missingRequiredField=true";
			
			User user = userService.findByUsername(principal.getName());
			
			// 주문 처리
			//Order order = orderService.createOrder(shoppingCart, shippingAddress, billingAddress, payment, shippingMethod, user);
			
			//mailSender.send(mailConstructor.constructOrderConfirmationEmail(user, order, Locale.KOREAN));
			
			// 장바구니 비우기
			//shoppingCartService.clearShoppingCart(shoppingCart);
		
			LocalDate today = LocalDate.now();
			
			LocalDate estimatedDeliveryDate;
			
			// 지역에 따른 날짜 처리
			if (shippingMethod.equals("groundShipping")) {
				estimatedDeliveryDate = today.plusDays(5);
			} else {
				estimatedDeliveryDate = today.plusDays(3);
			}
			
			model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);
			
			return "orderSubmittedPage";
	}
}

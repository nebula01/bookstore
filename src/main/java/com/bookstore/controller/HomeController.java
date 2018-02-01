package com.bookstore.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookstore.domain.Book;
import com.bookstore.domain.User;
import com.bookstore.domain.UserBilling;
import com.bookstore.domain.UserPayment;
import com.bookstore.domain.UserShipping;
import com.bookstore.domain.security.PasswordResetToken;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.service.BookService;
import com.bookstore.service.UserService;
import com.bookstore.service.impl.UserSecurityService;
import com.bookstore.utility.KRConstants;
import com.bookstore.utility.MailConstructor;
import com.bookstore.utility.SecurityUtility;

@Controller
public class HomeController {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailConstructor;

	@Autowired
	private UserService userService;

	@Autowired
	private UserSecurityService userSecurityService;

	@Autowired
	private BookService bookService;

	@RequestMapping("/")
	public String index() {
		return "index";
	}

	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("classActiveLogin", true);
		return "myAccount";
	}

	// 새 비밀번호
	@RequestMapping("/forgetPassword")
	public String forgetPassword(HttpServletRequest request, @ModelAttribute("email") String email, Model model) {

		model.addAttribute("classActiveForgetPassword", true);

		User user = userService.findByEmail(email);

		if (user == null) {
			model.addAttribute("emailNotExist", true);
			return "myAccount";
		}

		// 새 비밀번호 만들기
		String password = SecurityUtility.randomPassword();

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		userService.save(user);

		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);

		String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

		SimpleMailMessage newEmail = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user,
				password);

		mailSender.send(newEmail);

		model.addAttribute("forgetPasswordEmailSent", true);

		return "myAccount";
	}

	@RequestMapping(value = "/newUser", method = RequestMethod.POST)
	public String newUserPost(HttpServletRequest request, @ModelAttribute("email") String userEmail,
			@ModelAttribute("username") String username, Model model) throws Exception {
		model.addAttribute("classActiveNewAccount", true);
		model.addAttribute("email", userEmail);
		model.addAttribute("username", username);

		if (userService.findByUsername(username) != null) {
			model.addAttribute("usernameExists", true);

			return "myAccount";
		}

		if (userService.findByEmail(userEmail) != null) {
			model.addAttribute("emailExists", true);

			return "myAccount";
		}

		User user = new User();
		user.setUsername(username);
		user.setEmail(userEmail);

		String password = SecurityUtility.randomPassword();

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(user, role));
		userService.createUser(user, userRoles);

		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);

		String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

		SimpleMailMessage email = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user,
				password);

		mailSender.send(email);

		model.addAttribute("emailSent", "true");

		return "myAccount";
	}

	@RequestMapping("/newUser")
	public String newUser(Locale locale, @RequestParam("token") String token, Model model) {
		PasswordResetToken passToken = userService.getPasswordResetToken(token);

		if (passToken == null) {
			String message = "Invalid Token.";
			model.addAttribute("message", message);
			return "redirect:/badRequest";
		}

		User user = passToken.getUser();
		String username = user.getUsername();

		UserDetails userDetails = userSecurityService.loadUserByUsername(username);

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		model.addAttribute("user", user);

		model.addAttribute("classActiveEdit", true);
		return "myProfile";
	}

	// 책 전체 목록보기
	@RequestMapping("/bookShelf")
	public String bookShelf(Model model) {
		List<Book> book = bookService.findAll();

		model.addAttribute("bookList", book);
		return "bookShelf";
	}

	// 책 상세보기
	@RequestMapping("/bookDetail")
	// pathParam() url에 붙어오기 때문에 pathparam을 씀
	public String bookDetail(@PathParam("id") Long id, Model model, Principal principal) {

		// 유저 로그인 여부 체크
		if (principal != null) {
			String username = principal.getName();
			User user = userService.findByUsername(username);
			model.addAttribute("user", user);
		}

		// 책 상세 보기
		Book book = bookService.findOne(id);

		model.addAttribute("book", book);

		// 판매수량을 ArrayList로 만들어줌
		List<Integer> qtyList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		model.addAttribute("qtyList", qtyList);

		model.addAttribute("qty", 1); // 디폴트 값

		return "bookDetail";
	}

	// 사용자 프로필
	@RequestMapping("/myProfile")
	public String myProfile(Model model, Principal principal) {
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		model.addAttribute("userPaymentList", user.getUserPaymentList());
		model.addAttribute("userShippingList", user.getUserShippingList());
		// model.addAttribute("orderList", user.getOrderList());

		/*
		 * UserShipping userShipping = new UserShipping();
		 * model.addAttribute("userShipping", userShipping);
		 */

		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("listOfShippingAddresses", true);

		List<String> stateList = KRConstants.listOfKRStatesCode;
		Collections.sort(stateList);
		model.addAttribute("stateList", stateList);
		model.addAttribute("classActiveEdit", true);

		return "myProfile";
	}

	// 사용자 프로필에서 카드정보
	@RequestMapping("/listOfCreditCards")
	public String listOfCreditCards(Model model, Principal principal, HttpServletRequest req) {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		model.addAttribute("userPaymentList", user.getUserPaymentList());

		model.addAttribute("userShippingList", user.getUserShippingList());

		// model.addAttribute("orderList", user.OrderList());

		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveBilling", true);

		return "myProfile";

	}

	// 사용자 배송지 정보
	@RequestMapping("/listOfShippingAddresses")
	public String listOfShippingAddresses(Model model, Principal principal, HttpServletRequest req) {

		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);

		model.addAttribute("userPaymentList", user.getUserPaymentList());

		model.addAttribute("userShippingList", user.getUserShippingList());

		// model.addAttribute("orderList", user.OrderList());

		model.addAttribute("listOfCreditCards", true);
		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("classActiveBilling", true);

		return "myProfile";

	}

	// 결제 카드 get 처리
	@RequestMapping("/addNewCreditCard")
	public String addNewCreditCard(Model model, Principal principal) {
		
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);
			
		model.addAttribute("addNewCreditCard", true);
		
		model.addAttribute("classActiveBilling", true);
		
		model.addAttribute("listOfShippingAddresses", true);
		
		UserBilling userBilling = new UserBilling();
		
		UserPayment userPayment = new UserPayment();
		
		// 카드 정보의 청구지 주소
		model.addAttribute("userBilling", userBilling);
		
		model.addAttribute("userPayment", userPayment);
		
		List<String> stateList = KRConstants.listOfKRStatesCode;
		
		Collections.sort(stateList);
		
		model.addAttribute("stateList", stateList);
		
		model.addAttribute("userPaymentList", user.getUserPaymentList());

		model.addAttribute("userShippingList", user.getUserShippingList());

		// model.addAttribute("orderList", user.OrderList());
		
		return "myProfile";
	}
	
	// 결제 카드 post 처리
	@RequestMapping(value = "/addNewCreditCard", method = RequestMethod.POST)
	public String addNewCreditCardPost(
			@ModelAttribute("userPayment") UserPayment userPayment,
			@ModelAttribute("userBilling") UserBilling userBilling,
			Model model, Principal principal) {
		
		User user = userService.findByUsername(principal.getName());

		userService.updateUserBilling(userBilling, userPayment, user);
		
		// 사용자 정보
		model.addAttribute("user", user);
		
		// 지불 수단 정보
		model.addAttribute("userPaymentList", user.getUserPaymentList());
		
		// 배송 정보
		model.addAttribute("userShippingList", user.getUserShippingList());
		
		model.addAttribute("listOfCreditCards", true);
		
		model.addAttribute("listOfShippingAddresses", true);
		
		model.addAttribute("classActiveBilling", true);
				
		return "myProfile";
	}
	
	@RequestMapping("/addNewShippingAddress")
	public String addNewShippingAddress(Model model, Principal principal) {
		
		User user = userService.findByUsername(principal.getName());

		model.addAttribute("user", user);
			
		model.addAttribute("addNewShippingAddress", true);
		
		model.addAttribute("classActiveShipping", true);
		
		model.addAttribute("listOfCreditCards", true);
		
		UserShipping userShipping = new UserShipping();
		
		model.addAttribute("userShipping", userShipping);
		
		List<String> stateList = KRConstants.listOfKRStatesCode;
		
		Collections.sort(stateList);
		
		model.addAttribute("stateList", stateList);
		
		model.addAttribute("userPaymentList", user.getUserPaymentList());

		model.addAttribute("userShippingList", user.getUserShippingList());

		// model.addAttribute("orderList", user.OrderList());
		
		return "myProfile";
	}
}

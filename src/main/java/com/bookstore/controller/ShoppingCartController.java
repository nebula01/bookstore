package com.bookstore.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bookstore.domain.Book;
import com.bookstore.domain.CartItem;
import com.bookstore.domain.ShoppingCart;
import com.bookstore.domain.User;
import com.bookstore.service.BookService;
import com.bookstore.service.CartItemService;
import com.bookstore.service.ShoppingCartService;
import com.bookstore.service.UserService;

@Controller
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private BookService bookService;
	
	@RequestMapping("/cart")
	public String shoppingCart(Model model, Principal principal) {

		User user = userService.findByUsername(principal.getName());
		// System.out.println(user.getUsername());

		ShoppingCart shoppingCart = user.getShoppingCart();
		// System.out.println(shoppingCart);
		List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

		shoppingCartService.updateShoppingCart(shoppingCart);

		model.addAttribute("user", user);
		model.addAttribute("cartItemList", cartItemList);
		model.addAttribute("shoppingCart", shoppingCart);

		return "shoppingCart";
	}

	// 장바구니에 들어갈 목록 추가 
	@RequestMapping(value = "/addItem", method =  RequestMethod.POST)
	public String addItem(@ModelAttribute("book") Book book, 
			@ModelAttribute("qty") String qty, Model model,
			Principal principal) {
		// ModelAttribute는 requestParameter로 받아오는 것과 같다,
		// setter메소드를 통해 바인딩
		
		User user = userService.findByUsername(principal.getName());
	
		book = bookService.findOne(book.getId());
		
		// 책 재고 보다 넣으려는 권 수량이 많을 경운
		if (Integer.parseInt(qty) > book.getInStockNumber()) {
			model.addAttribute("notEnoughStock", true);
			return "forward:/bookDetail?id="+book.getId(); // 책 상세보기로 forward
		}
		
		CartItem cartItem = cartItemService.addBookToCartItem(book, user, Integer.parseInt(qty)); 
		
		model.addAttribute("addBookSuccess", true);
		
		return "forward:/bookDetail?id="+book.getId();
	}
}

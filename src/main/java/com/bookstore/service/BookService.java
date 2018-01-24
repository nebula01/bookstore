package com.bookstore.service;

import java.util.List;

import com.bookstore.domain.Book;

public interface BookService {

	// 책 목록 전체보기
	List<Book> findAll();
	
	// 책 상세보기
	Book findOne(Long id);
}

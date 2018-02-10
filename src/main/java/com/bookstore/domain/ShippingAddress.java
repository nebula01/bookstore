package com.bookstore.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ShippingAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String shippingAddressName;
	private String shippingAddressStreet;
	private String shippingAddressCity;
	private String shippingAddressState;
	private String shippingAddressCountry;
	private String shippingAddressZipcode;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getshippingAddressName() {
		return shippingAddressName;
	}

	public void setshippingAddressName(String shippingAddressName) {
		this.shippingAddressName = shippingAddressName;
	}

	public String getshippingAddressStreet() {
		return shippingAddressStreet;
	}

	public void setshippingAddressStreet(String shippingAddressStreet) {
		this.shippingAddressStreet = shippingAddressStreet;
	}

	public String getshippingAddressCity() {
		return shippingAddressCity;
	}

	public void setshippingAddressCity(String shippingAddressCity) {
		this.shippingAddressCity = shippingAddressCity;
	}

	public String getshippingAddressState() {
		return shippingAddressState;
	}

	public void setshippingAddressState(String shippingAddressState) {
		this.shippingAddressState = shippingAddressState;
	}

	public String getshippingAddressCountry() {
		return shippingAddressCountry;
	}

	public void setshippingAddressCountry(String shippingAddressCountry) {
		this.shippingAddressCountry = shippingAddressCountry;
	}

	public String getshippingAddressZipcode() {
		return shippingAddressZipcode;
	}

	public void setshippingAddressZipcode(String shippingAddressZipcode) {
		this.shippingAddressZipcode = shippingAddressZipcode;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}

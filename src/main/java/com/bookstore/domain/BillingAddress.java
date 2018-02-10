package com.bookstore.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class BillingAddress {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String billingAddressName;
	private String billingAddressStreet;
	private String billingAddressCity;
	private String billingAddressState;
	private String billingAddressCountry;
	private String billingAddressZipcode;

	@OneToOne(cascade = CascadeType.ALL)
	private Order order;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getbillingAddressName() {
		return billingAddressName;
	}

	public void setbillingAddressName(String billingAddressName) {
		this.billingAddressName = billingAddressName;
	}

	public String getbillingAddressStreet() {
		return billingAddressStreet;
	}

	public void setbillingAddressStreet(String billingAddressStreet) {
		this.billingAddressStreet = billingAddressStreet;
	}

	public String getbillingAddressCity() {
		return billingAddressCity;
	}

	public void setbillingAddressCity(String billingAddressCity) {
		this.billingAddressCity = billingAddressCity;
	}

	public String getbillingAddressState() {
		return billingAddressState;
	}

	public void setbillingAddressState(String billingAddressState) {
		this.billingAddressState = billingAddressState;
	}

	public String getbillingAddressCountry() {
		return billingAddressCountry;
	}

	public void setbillingAddressCountry(String billingAddressCountry) {
		this.billingAddressCountry = billingAddressCountry;
	}

	public String getbillingAddressZipcode() {
		return billingAddressZipcode;
	}

	public void setbillingAddressZipcode(String billingAddressZipcode) {
		this.billingAddressZipcode = billingAddressZipcode;
	}
}

package com.model;

public class Recipient {
	private final String name;
	private final String company;
	private final String emailId;

	public Recipient(final String name, final String emailId, final String company) {
		this.name = name;
		this.company = company;
		this.emailId = emailId;
	}

	public String getName() {
		return this.name;
	}

	public String getCompany() {
		return this.company;
	}

	public String getEmailId() {
		return this.emailId;
	}
}

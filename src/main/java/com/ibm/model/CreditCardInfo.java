package com.ibm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class CreditCardInfo {
    private String firstName;
    private String lastName;
    private int expiredMonth;
    private int expiredYear;
    private int securityCode;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getExpiredMonth() {
		return expiredMonth;
	}
	public void setExpiredMonth(int expiredMonth) {
		this.expiredMonth = expiredMonth;
	}
	public int getExpiredYear() {
		return expiredYear;
	}
	public void setExpiredYear(int expiredYear) {
		this.expiredYear = expiredYear;
	}
	public int getSecurityCode() {
		return securityCode;
	}
	public void setSecurityCode(int securityCode) {
		this.securityCode = securityCode;
	}
}

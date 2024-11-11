package project.hotelsystem.database.models;

public class customer {
private String guest_id;
private String guest_name;
private String phone_no;
private String identity_card;
private String email;

public customer(String guest_id, String guest_name, String phone_no, String identity_card, String email) {
	super();
	this.guest_id = guest_id;
	this.guest_name = guest_name;
	this.phone_no = phone_no;
	this.identity_card = identity_card;
	this.email = email;
}

	public customer(String guest_name, String phone_no) {
		this.guest_name = guest_name;
		this.phone_no = phone_no;
	}

	public String getGuest_id() {
	return guest_id;
}
public void setGuest_id(String guest_id) {
	this.guest_id = guest_id;
}
public String getGuest_name() {
	return guest_name;
}
public void setGuest_name(String guest_name) {
	this.guest_name = guest_name;
}
public String getPhone_no() {
	return phone_no;
}
public void setPhone_no(String phone_no) {
	this.phone_no = phone_no;
}
public String getIdentity_card() {
	return identity_card;
}
public void setIdentity_card(String identity_card) {
	this.identity_card = identity_card;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}
public customer(String guest_id) {
	this.guest_id=guest_id;
}
}
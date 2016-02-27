package com.firkinofbrain.blackout.database.user;

import com.firkinofbrain.blackout.database.ModelBase;

public class User extends ModelBase{
	
	private String unique_id;
	private String name;
	private String avatar;
	private String city;
	private String country;
	private String sex;
	private int age;
	private int weight;
	private int session;
	
	public String getUserId() {
		return unique_id;
	}
	public void setUserId(String id) {
		this.unique_id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getSession() {
		return session;
	}
	public void setSession(int session) {
		this.session = session;
	}
	public String getSex() {
		return this.sex;
	}
	public void setSex(String sex){
		if(sex == "male" || sex == "female") this.sex = sex;
		else this.sex = "male";
	}
	
}

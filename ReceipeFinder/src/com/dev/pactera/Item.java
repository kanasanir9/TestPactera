package com.dev.pactera;

import java.util.Date;

public class Item {

	private String name;
	private int amount;
	private Date useBy;
	

	private UNITS unit;

	public enum UNITS {
		of, grams, ml, slices;
		
	
	};

	private Item() {

	}
    
	public Item(String name, int amount, UNITS unt, Date useBy) {
		this.name = name;
		this.amount = amount;
		this.useBy = useBy;
		this.unit = unt;

	}
	
	public boolean isExpired() {
		return this.useBy.before(new Date());
	}
	
	public Date getUseBy() {
		return useBy;
	}
	
	public boolean isSufficient(int amount) {
		//TODO check for unit as well
		return this.amount >= amount;
		
	}
}

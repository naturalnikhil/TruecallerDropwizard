package com.truecaller.hw.core;

import java.io.Serializable;
import java.util.Calendar;

import javax.validation.constraints.NotNull;

public class Viewer implements Serializable{

	private static final long serialVersionUID = 1L;

	// id of viewer
	@NotNull
	private Long id;
	
	// server-time of view : date can be derived from it as per wish
	@NotNull
	private Long timestamp;
	
	@NotNull
	private String dateTime;
	
	public Viewer() 
	{
		timestamp=System.currentTimeMillis();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		dateTime = "Time=" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) +" Date=" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" +  calendar.get(Calendar.YEAR);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public String getDateTime() {
		return dateTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Viewer other = (Viewer) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}

package com.truecaller.hw.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "person")
public class Person 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // saving viewers of this user's profile in json form : else a separate table would have been needed and a lot of DELETE needs to be issued for removing obsolete objects
    // during processing this field will be converted to list and processed and serialised back to list
    @Column(name = "viewers", length=1000000)
    private String viewers;

    public Person() {}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getViewers() {
		return viewers;
	}

	public void setViewers(String viewers) {
		this.viewers = viewers;
	}
}
package com.drestaurant.common.domain.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;

/**
 *
 *  A class which encapsulates 'who' and 'when' has perform some action
 *
 * @author: idugalic
 * Date: 4/29/18
 * Time: 3:44 PM
 */
public class AuditEntry {

	private String who;
	private Date when;

	public AuditEntry() {

	}

	public AuditEntry(String who, Date when) {
		super();
		this.who = who;
		this.when = when;
	}

	public AuditEntry(String who) {
		this(who, new Date());
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public Date getWhen() {
		return when;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}

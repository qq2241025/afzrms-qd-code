package com.dt.afzrms.po;

// Generated 2015-1-22 18:06:40 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * RefUserTerminal generated by hbm2java
 */
@Entity
@Table(name = "REF_USER_TERMINAL", catalog = "qdafz")
public class RefUserTerminal implements java.io.Serializable {

	private Integer id;
	private TUser TUser;
	private TTerminal TTerminal;

	public RefUserTerminal() {
	}

	public RefUserTerminal(TUser TUser, TTerminal TTerminal) {
		this.TUser = TUser;
		this.TTerminal = TTerminal;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public TUser getTUser() {
		return this.TUser;
	}

	public void setTUser(TUser TUser) {
		this.TUser = TUser;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id")
	public TTerminal getTTerminal() {
		return this.TTerminal;
	}

	public void setTTerminal(TTerminal TTerminal) {
		this.TTerminal = TTerminal;
	}

}
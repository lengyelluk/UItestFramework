package com.test.lengyel.gui;

public class GuiElement {

	private String name;
	private FrameworkKindEnum kind;
	private String identifier;
	private String identValue;
	private String staticIdentValue;
	private String value;
	private boolean ajax;
	private boolean http;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FrameworkKindEnum getKind() {
		return kind;
	}

	public void setKind(FrameworkKindEnum kind) {
		this.kind = kind;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentValue() {
		return identValue;
	}
	
	public void setIdentValue(String identValue) {
		this.identValue = identValue;
	}
	
	public String getStaticIdentValue() {
		return staticIdentValue;
	}

	public void setStaticIdentValue(String staticIdentValue) {
		this.staticIdentValue = staticIdentValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isAjax() {
		return ajax;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public boolean isHttp() {
		return http;
	}

	public void setHttp(boolean http) {
		this.http = http;
	}

	@Override
	public String toString() {
		return "GuiElement [name=" + name + ", kind=" + kind
				+ ", identifier=" + identifier + ", identValue=" + identValue
				+ ", staticIdentValue=" + staticIdentValue + ", value=" + value
				+ ", ajax=" + ajax + ", http=" + http + "]";
	}
	
}

package com.spring.demo.springbootexample.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
//
@ApiModel(value = "产品信息")
public class Product {

	@ApiModelProperty(required = true, name = "name", value = "产品名称", dataType = "query")
	private String name;
	@ApiModelProperty(name = "type", value = "产品类型", dataType = "query")
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Product{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				'}';
	}
}

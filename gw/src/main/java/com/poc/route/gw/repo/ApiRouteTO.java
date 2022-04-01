package com.poc.route.gw.repo;

import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class ApiRouteTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5022914952544229566L;
	
	private String apiRouteId;
	private String name;
	private JsonNode filter;
	private JsonNode predicate;
	private String url;

}

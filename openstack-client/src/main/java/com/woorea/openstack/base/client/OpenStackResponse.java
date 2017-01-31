package com.woorea.openstack.base.client;

/*
 * Modifications copyright (c) 2017 AT&T Intellectual Property
 */

import java.io.InputStream;
import java.util.Map;

public interface OpenStackResponse {

	public <T> T getEntity(Class<T> returnType);

	public <T> T getErrorEntity(Class<T> returnType);

	public InputStream getInputStream();

	public String header(String name);
	
	public Map<String, String> headers();
	
}

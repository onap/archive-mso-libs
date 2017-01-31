/*
 * ============LICENSE_START==========================================
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 *
 * ECOMP and OpenECOMP are trademarks
 * and service marks of AT&T Intellectual Property.
 *
 */

package com.woorea.openstack.connector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonRootName;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.woorea.openstack.base.client.OpenStackClientConnector;
import com.woorea.openstack.base.client.OpenStackConnectException;
import com.woorea.openstack.base.client.OpenStackRequest;
import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.base.client.OpenStackResponseException;

public class HttpClientConnector implements OpenStackClientConnector {

	public static ObjectMapper DEFAULT_MAPPER;
	public static ObjectMapper WRAPPED_MAPPER;
	
	private static Logger LOGGER = Logger.getLogger(HttpClientConnector.class);

	static {
		DEFAULT_MAPPER = new ObjectMapper();

		DEFAULT_MAPPER.setSerializationInclusion(Inclusion.NON_NULL);
		DEFAULT_MAPPER.disable(SerializationConfig.Feature.INDENT_OUTPUT);
		DEFAULT_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		DEFAULT_MAPPER.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);

		WRAPPED_MAPPER = new ObjectMapper();

		WRAPPED_MAPPER.setSerializationInclusion(Inclusion.NON_NULL);
		WRAPPED_MAPPER.disable(SerializationConfig.Feature.INDENT_OUTPUT);
		WRAPPED_MAPPER.enable(SerializationConfig.Feature.WRAP_ROOT_VALUE);
		WRAPPED_MAPPER.enable(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE);
		WRAPPED_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		WRAPPED_MAPPER.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	
	protected static <T> ObjectMapper getObjectMapper (Class<T> type) {
		return type.getAnnotation(JsonRootName.class) == null ? DEFAULT_MAPPER : WRAPPED_MAPPER;
	}

	public <T> OpenStackResponse request(OpenStackRequest<T> request) {
		
		CloseableHttpClient httpClient = null; //HttpClients.createDefault();
		httpClient = HttpClients.custom().setRedirectStrategy(new HttpClientRedirectStrategy()).build();

		URI uri = null;
		
		// Build the URI with query params
		try {
			URIBuilder uriBuilder = new URIBuilder(request.endpoint() + request.path());

			for(Map.Entry<String, List<Object> > entry : request.queryParams().entrySet()) {
				for (Object o : entry.getValue()) {
					uriBuilder.setParameter(entry.getKey(), String.valueOf(o));
				}
			}
			
			uri = uriBuilder.build();
		} catch (URISyntaxException e) {
			throw new HttpClientException (e);
		}

		HttpEntity entity = null;
		if (request.entity() != null) {
			// Flatten the entity to a Json string
					
			try {
		    	// Get appropriate mapper, based on existence of a root element in Entity class
				ObjectMapper mapper = getObjectMapper (request.entity().getEntity().getClass());

				String entityJson = mapper.writeValueAsString (request.entity().getEntity());
				entity = new StringEntity(entityJson, ContentType.create(request.entity().getContentType()));
				
				System.out.println("Openstack query JSON:"+entityJson);
				LOGGER.debug ("Request JSON Body: " + entityJson.replaceAll("\"password\":\"[^\"]*\"", "\"password\":\"***\""));

			} catch (JsonProcessingException e) {
				throw new HttpClientException ("Json processing error on request entity", e);
			} catch (IOException e) {
				throw new HttpClientException ("Json IO error on request entity", e);
			}
		}
		
		// Determine the HttpRequest class based on the method
		HttpUriRequest httpRequest;
		
		switch (request.method()) {
		case POST:
			HttpPost post = new HttpPost(uri);
			post.setEntity (entity);
			httpRequest = post;
			break;
			
		case GET:
			httpRequest = new HttpGet(uri);
			break;

		case PUT:
			HttpPut put = new HttpPut(uri);
			put.setEntity (entity);
			httpRequest = put;
			break;
			
		case DELETE:
			httpRequest = new HttpDelete(uri);
			break;
			
		default:
			throw new HttpClientException ("Unrecognized HTTP Method: " + request.method());
		}
		
		for (Entry<String, List<Object>> h : request.headers().entrySet()) {
			StringBuilder sb = new StringBuilder();
			for (Object v : h.getValue()) {
				sb.append(String.valueOf(v));
			}
			httpRequest.addHeader(h.getKey(), sb.toString());
		}

		LOGGER.debug ("Sending HTTP request: " + httpRequest.toString());
		
		// Get the Response.  But don't get the body entity yet, as this response
		// will be wrapped in an HttpClientResponse.  The HttpClientResponse
		// buffers the body in constructor, so can close the response here.
		HttpClientResponse httpClientResponse = null;
		CloseableHttpResponse httpResponse = null;
		
		// Catch known HttpClient exceptions, and wrap them in OpenStack Client Exceptions
		// so calling functions can distinguish.  Only RuntimeExceptions are allowed.
		try {
			httpResponse = httpClient.execute(httpRequest);

			LOGGER.debug ("Response status: " + httpResponse.getStatusLine().getStatusCode());
			
			httpClientResponse = new HttpClientResponse (httpResponse);

			int status = httpResponse.getStatusLine().getStatusCode();
			if (status == HttpStatus.SC_OK || status == HttpStatus.SC_CREATED ||
				status == HttpStatus.SC_NO_CONTENT || status == HttpStatus.SC_ACCEPTED)
			{
				return httpClientResponse;
			}
		}
		catch (HttpResponseException e) {
			// What exactly does this mean?  It does not appear to get thrown for
			// non-2XX responses as documented.
			throw new OpenStackResponseException(e.getMessage(), e.getStatusCode());
		}
		catch (UnknownHostException e) {
			throw new OpenStackConnectException("Unknown Host: " + e.getMessage());
		}
		catch (IOException e) {
			// Catch all other IOExceptions and throw as OpenStackConnectException
			throw new OpenStackConnectException(e.getMessage());
		}
		catch (Exception e) {
			// Catchall for anything else, must throw as a RuntimeException
			e.printStackTrace();
			throw new RuntimeException("Unexpected client exception", e);
		}
		finally {
			// Have the body.  Close the stream
			if (httpResponse != null)
				try {
					httpResponse.close();
				} catch (IOException e) {
					LOGGER.warn("Unable to close HTTP Response: " + e);
				}
		}
		
		// Get here on an error response (4XX-5XX)
		throw new OpenStackResponseException(httpResponse.getStatusLine().getReasonPhrase(),
											httpResponse.getStatusLine().getStatusCode(),
											httpClientResponse);
	}

}

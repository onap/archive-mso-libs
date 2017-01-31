package com.woorea.openstack.heat.model;

/*
 * Modifications copyright (c) 2017 AT&T Intellectual Property
 */

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

// There is no Root element for the Explanation return
//@JsonRootName("error")
public class Explanation {
    @JsonProperty("explanation")
    private String explanation;

    @JsonProperty("code")
    private int code;

    @JsonProperty("title")
    private String title;
    
    @JsonRootName("error")
    public static class Error {
        @JsonProperty("message")
        private String message;

        @JsonProperty("traceback")
        private String traceback;

        @JsonProperty("type")
        private String type;

        public String getMessage() {
        	return message;
        }
        
        public String getTraceback() {
        	return traceback;
        }
        
        public String getType() {
        	return type;
        }
    }
 
    private Error error;
    
    public String getExplanation() {
    	return explanation;
    }
    
    public int getCode() {
    	return code;
    }
    
    public String getTitle() {
    	return title;
    }
    
    public Error getError() {
    	return error;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Explanation [ " +
				"code='" + code +
				"', title='" + title +
				"', explanation='" + explanation +
				"', Error [type='" + error.type +
				"', message='" + error.message + "' ] ]";
	}

}

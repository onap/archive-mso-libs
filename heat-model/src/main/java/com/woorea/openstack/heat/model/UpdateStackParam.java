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

package com.woorea.openstack.heat.model;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

public class UpdateStackParam {
    @JsonProperty("template_url")
    private String templateUrl;

    @JsonProperty
    private String template;

    @JsonProperty("parameters")
    private Map<String, Object> parameters;

    @JsonProperty("timeout_mins")
    private int timeoutMinutes;

    @JsonProperty("environment")
    private String environment;

    @JsonProperty("disable_rollback")
    private boolean disableRollback = true;
    
    @JsonProperty("files")
    private Map<String, Object> files;

    public String getTemplateUrl() {
        return templateUrl;
    }

    /**
     * The URL of the template to instantiate. This value is ignored if the template is supplied inline.
     *
     * @param templateUrl a template url.
     */
    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getTemplate() {
        return template;
    }

    /**
     * A JSON template to instantiate. This value takes precedence over the template URL if both are supplied.
     *
     * @param template a template json.
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public int getTimeoutMinutes() {
        return timeoutMinutes;
    }

    public void setTimeoutMinutes(int timeoutMinutes) {
        this.timeoutMinutes = timeoutMinutes;
    }

    public String getEnvironment() {
        return environment;
    }    

    public void setFiles(Map<String, Object> files) {
    	this.files = files;
    }
    public Map<String, Object> getFiles() {
    	return this.files;
    }

    /**
     * A JSON environment for the stack.
     *
     * @param environment a environment.
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public boolean getDisableRollback() {
        return disableRollback;
    }

    public void setDisableRollback(boolean disableRollback) {
        this.disableRollback = disableRollback;
    }

    @Override
    public String toString() {
        return "UpdateStackParam{" +
                " templateUrl='" + templateUrl + '\'' +
                ", template='" + template + '\'' +
                ", parameters=" + parameters +
                ", timeoutMinutes=" + timeoutMinutes +
                ", environment='" + environment + '\'' +
                ", disableRollback='" + disableRollback + '\'' +
                ", files=" + files +
                '}';
    }
}

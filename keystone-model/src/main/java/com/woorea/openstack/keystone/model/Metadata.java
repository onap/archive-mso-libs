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

package com.woorea.openstack.keystone.model;

import java.util.Map;

/**
 * Metadata extension for Tenants as implemented by DCP/LCP.
 * 
 * NOTE: This is NOT supported by native Openstack
 */
public class Metadata {

	private Map<String, String> metadata;

	/**
	 * @return the metadata
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

	/**
	 * Set the metadata
	 * @param metadata
	 */
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}
	

	
}

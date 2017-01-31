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

package com.woorea.openstack.base.client;

/**
 * A common abstract parent of all Openstack Exception types, allowing
 * calling classes the choice to catch all error exceptions together.
 */
public abstract class OpenStackBaseException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	/*
	 * Implement only the basic constructors
	 */
	public OpenStackBaseException () {}
	
	public OpenStackBaseException(String message) {
		super(message);
	}

	public OpenStackBaseException(String message, Throwable cause) {
		super(message, cause);
	}
}

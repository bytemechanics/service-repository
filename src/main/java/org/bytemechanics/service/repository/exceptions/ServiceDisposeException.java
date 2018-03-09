/*
 * Copyright 2017 Byte Mechanics.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bytemechanics.service.repository.exceptions;

import org.bytemechanics.service.repository.ServiceSupplier;
import org.bytemechanics.service.repository.internal.commons.string.SimpleFormat;

/**
 * Exception thrown when service can not be disposed for some reason
 * @author afarre
 * @since 0.1.0
 */
public class ServiceDisposeException extends RuntimeException{

	private final String serviceName;
	
	/**
	 * Service dispose exception constructor
	 * @param _serviceName Service name should be obtained from the corresponding ServiceSupplier
	 * @param _message Descriptive message of the reason because service can not be disposed
	 * @param _cause Underlaying ause of the service dispose failure
	 */
	public ServiceDisposeException(final String _serviceName,final String _message,final Throwable _cause) {
		super(SimpleFormat.format("Service {} can not be disposed properly: {}", _serviceName,_message),_cause);
		this.serviceName=_serviceName;
	}

	
	/**
	 * Service name that can not be disposed obtained from the corresponding ServiceSupplier
	 * @return Service name
	 * @see ServiceSupplier
	 */
	public String getServiceName() {
		return serviceName;
	}
}

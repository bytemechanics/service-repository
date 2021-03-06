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

import java.util.Objects;
import org.bytemechanics.service.repository.ServiceSupplier;
import org.bytemechanics.service.repository.internal.commons.string.SimpleFormat;

/**
 * Exception thrown when service can not be initialized for some reason
 * @author afarre
 * @since 0.1.0
 */
public class ServiceInitializationException extends RuntimeException{

	private final String serviceName;
	
	/**
	 * Service initialization exception constructor
	 * @param _serviceName Service name should be obtained from the corresponding ServiceSupplier
	 * @param _message Descriptive message of the reason because service can not be instantiated
	 */
	public ServiceInitializationException(final String _serviceName,final String _message) {
		this(_serviceName,_message,null);
	}
	/**
	 * Service initialization exception constructor
	 * @param _serviceName Service name should be obtained from the corresponding ServiceSupplier
	 * @param _message Descriptive message of the reason because service can not be instantiated
	 * @param _cause Underlaying ause of the service initialization failure
	 */
	public ServiceInitializationException(final String _serviceName,final String _message,final Throwable _cause) {
		super(SimpleFormat.format("Service {} can not be initialized properly: {}", _serviceName,_message),_cause);
		this.serviceName=_serviceName;
	}

	
	/**
	 * Service name that can not be initialized obtained from the corresponding ServiceSupplier
	 * @return Service name
	 * @see ServiceSupplier
	 */
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.serviceName);
		hash = 97 * hash + Objects.hashCode(getMessage());
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ServiceInitializationException other = (ServiceInitializationException) obj;
		if (!Objects.equals(this.serviceName, other.serviceName)) {
			return false;
		}
		return Objects.equals(getMessage(), other.getMessage());
	}
	
}

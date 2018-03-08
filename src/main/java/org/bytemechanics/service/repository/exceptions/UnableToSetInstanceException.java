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

import org.bytemechanics.service.repository.internal.commons.string.SimpleFormat;

/**
 * Exception thrown when can not set an instance that does not implement or extend the required adapter
 * @author afarre
 * @since 1.0.2
 * @version 1.0.0
 */
public class UnableToSetInstanceException extends RuntimeException {

	/**
	 * Message format to use
	 */
	protected static final String MESSAGE="Unable to set instance {} that doesn't implement the required adapter {}";


	/**
	 * Unable to set instance exception constructor
	 * @param _instance instance that is trying to assign
	 * @param _adapter adapter that instance should extend/implement
	 * @since 1.0.0
	 */
	public UnableToSetInstanceException(final Object _instance,final Class _adapter) {
		super(SimpleFormat.format(MESSAGE,_instance,_adapter));
	}
}

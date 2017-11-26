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
package org.bytemechanics.service.repository;

import java.io.Closeable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bytemechanics.service.repository.exceptions.ServiceDisposeException;
import org.bytemechanics.service.repository.exceptions.ServiceInitializationException;
import org.bytemechanics.service.repository.internal.ObjectFactory;

/**
 * Service supplier interface to store all service metadata information and the current instance in case of singletons
 * @author afarre
 * @since 0.1.0
 */
public interface ServiceSupplier extends Supplier {

	/**
	 * Method to return the service name
	 * @return Service name
	 */
	public String getName();
	/**
	 * Method to return the service adapter interface that any implementation must accomplish in order to be an eligible implementation
	 * @return Service adapter interface
	 */
	public Class getAdapter();
	/**
	 * Method to return if the service must be instantiated as singleton (only on instance in all virtual machine)
	 * @return true if the this service is a singleton
	 */
	public boolean isSingleton();
	/**
	 * Method to return the current supplier for this service
	 * @return the Supplier for this service
	 * @see Supplier
	 */
	public Supplier getSupplier();

	/**
	 * Method to retrieve the current singleton service instance
	 * @return singleton service instance or null if is not a singleton or still not instantiated
	 */
	public Object getInstance();
	/**
	 * Method to store the new singleton service instance
	 * @param _instance instance to store
	 */
	public void setInstance(final Object _instance);	
	
	/**
	 * Method to obtain the service instance, note that if the service is a singleton always returns the same instance
	 * @return the optional service instance as Object that can be empty if the instance can not be obtained
	 */
	@SuppressWarnings("DoubleCheckedLocking")
	public default Optional<Object> tryGet() {
		
		Optional<Object> reply=Optional.empty();

		try{
			reply=Optional.ofNullable(get());
		}catch(Throwable e){
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,e,() -> MessageFormat.format("service::supplier::service::{0}::get::fail::{1}",getName(),e.getMessage()));
		}
				
		return reply;
	}
	
	/**
	 * Method to obtain the service instance, if is a singleton synchronized set the instance with #setInstance(instance) <br>
	 * Note that if the service is a singleton always returns the same instance
	 * @return the service instance as Object or null if the instance can not be obtained
	 * @throws ServiceInitializationException when service can not be instantiated
	 * @see Supplier#get() 
	 */
	@Override
	@SuppressWarnings("DoubleCheckedLocking")
	public default Object get() {
		
		Object reply;

		if(isSingleton()){
			Object current=getInstance();
			if(current==null){
				synchronized(this){
					current=getInstance();
					if(current==null){
						setInstance(getSupplier().get());
					}			
				}
			}
			reply=current;
		}else{
			reply=getSupplier().get();
		}
				
		return reply;
	}
	
	/**
	 * Service initialization; if is singleton then instantiate it by calling #get(), otherwise does nothing
	 * @throws ServiceInitializationException when service can not be instantiated
	 * @see #get() 
	 */
	public default void init(){

		if(isSingleton()){
			get();
		}
	}
	/**
	 * Synchronized service dispose. if is singleton AND implements Closeable then dispose it by calling Closeable#close() and removes current instance by calling #setInstance(null), otherwise does nothing
	 * @throws ServiceDisposeException when service can not be disposed
	 * @see Closeable#close() 
	 */
	@SuppressWarnings("UseSpecificCatch")
	public default void dispose(){
		
		if(isSingleton()){
			Object current=getInstance();
			if(current!=null){
				synchronized(this){
					current=getInstance();
					if(current!=null){
						if(Closeable.class.isAssignableFrom(getAdapter())){
							try {
								((Closeable)current).close();
							} catch (Throwable e) {
								throw new ServiceDisposeException(getName(),e.getMessage(),e);
							}
						}
						setInstance(null);
					}			
				}
			}
		}
	}
	
	
	/**
	 * Utility method to generate implementation suppplier form the class implementation
	 * @param <T> implementation class type
	 * @param _name service name
	 * @param _implementation implementation class
	 * @param _attributes attributes to use when instantiate implementation class
	 * @return Instance supplier of the service implementation
	 * @see Supplier
	 */
	public static <T> Supplier<T> generateSupplier(final String _name,final Class<T> _implementation,final Object... _attributes){
		return () -> ObjectFactory.of(_implementation)
							.with(_attributes)
							.supplier()
							.get()
							.orElseThrow(() -> new ServiceInitializationException(_name,MessageFormat
																	.format("Unable to instantiate service with class {0} using constructor({1})", 
																		_implementation,
																		Optional.ofNullable(_attributes)
																				.map(attributesArray -> Arrays.asList(attributesArray))
																				.orElse(Collections.emptyList()))));
	}
}

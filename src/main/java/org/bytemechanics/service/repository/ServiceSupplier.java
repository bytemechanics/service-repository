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
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bytemechanics.service.repository.beans.DefaultServiceSupplier;
import org.bytemechanics.service.repository.exceptions.ServiceDisposeException;
import org.bytemechanics.service.repository.exceptions.ServiceInitializationException;
import org.bytemechanics.service.repository.internal.commons.reflection.ObjectFactory;
import org.bytemechanics.service.repository.internal.commons.string.SimpleFormat;

/**
 * Service supplier interface to store all service metadata information and the current instance in case of singletons
 * @author afarre
 * @since 0.1.0
 * @version 1.1.0
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
	 * Method to return the service implementation class
	 * @return Service class that implements getAdapter()
	 */
	public Class getImplementation();
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
	 * Method to store a new supplier service instance
	 * @param _supplier supplier to provides service instance 
	 * @since v1.2.0
	 */
	public void setSupplier(final Supplier _supplier);		
	/**
	 * Method to reset suplier and instance to it's original status this method does not dispose correctly any contained instance for this purpose exist the dispose method
	 * @see ServiceSupplier#dispose() 
	 * @since v1.3.0
	 */
	public void reset();
	/**
	 * Method to return the current supplier for this service or if the _args it's not null nor empty returns a new supplier with these arguments.
	 * @param _args arguments to use to create the supplier (optional)
	 * @return the Supplier for this service
	 * @see Supplier
	 * @since 1.2.0
	 */
	public default Supplier provideSupplier(final Object... _args){
		return (Supplier)Optional.ofNullable(_args)
							.filter(arguments -> arguments.length>0)
							.filter(arguments -> getImplementation()!=null)
							.map(arguments -> generateSupplier(getName(),getImplementation(), arguments))
							.orElse(getSupplier());
	}

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
	 * Method to obtain the service instance, note that if the service is a singleton always returns the same instance.
	 * if _args it's not null nor empty and doesn't exist any instance, a new supplier is generated with this new parameters and used ony for this occasion.
	 * @param _args arguments to use to instance in case its not singleton and already instanced
	 * @return the optional service instance as Object that can be empty if the instance can not be obtained
	 */
	@SuppressWarnings("DoubleCheckedLocking")
	public default Optional<Object> tryGet(final Object... _args) {
		
		Optional<Object> reply=Optional.empty();

		try{
			reply=Optional.ofNullable(get(_args));
		}catch(Throwable e){
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,e,() -> SimpleFormat.format("service::supplier::service::{}::get::fail::{}",getName(),e.getMessage()));
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
	public default Object get() {
		return get(new Object[0]);
	}

	
	/**
	 * Method to obtain the service instance, if is a singleton synchronized set the instance with #setInstance(instance) <br>
	 * Note that if the service is a singleton always returns the same instance if _args it's not null nor empty and doesn't exist any instance,
	 * a new supplier is generated with this new parameters and used ony for this occasion.
	 * @param _args arguments to use to instance in case its not singleton and already instanced
	 * @return the service instance as Object or null if the instance can not be obtained
	 * @throws ServiceInitializationException when service can not be instantiated
	 */
	@SuppressWarnings("DoubleCheckedLocking")
	public default Object get(final Object... _args) {
		
		Object reply;

		if(isSingleton()){
			Object current=getInstance();
			if(current==null){
				synchronized(this){
					current=getInstance();
					if(current==null){
						current=provideSupplier(_args).get();
						setInstance(current);
					}			
				}
			}
			reply=current;
		}else{
			reply=provideSupplier(_args).get();
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
	 * Synchronized service dispose. if is singleton AND, THE IMPLEMENTATION, implements Closeable then dispose it will call Closeable#close() and removes current instance by calling #setInstance(null), otherwise does nothing
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
						if(Closeable.class.isAssignableFrom(current.getClass())){
							try {
								((Closeable)current).close();
							} catch (Throwable e) {
								throw new ServiceDisposeException(getName(),e.getMessage(),e);
							}
						}
						reset();
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
							.orElseThrow(() -> new ServiceInitializationException(_name,SimpleFormat
																	.format("Unable to instantiate service with class {} using constructor({})", 
																		_implementation,
																		Optional.ofNullable(_attributes)
																				.map(Arrays::asList)
																				.orElse(Collections.emptyList()))));
	}
	
	
	/**
	 * Constructor of service supplier
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _iimplementation must implement
	 * @param _supplier adapter class implementation supplier
	 * @return default ServiceSupplier instance
	 * @see DefaultServiceSupplier#DefaultServiceSupplier(java.lang.String, java.lang.Class, java.util.function.Supplier) 
	 * @since 1.3.0
	 */
	public static <T extends Object> ServiceSupplier from(final String _name,final Class<T> _adapter,final Supplier<? extends T> _supplier){
		return new DefaultServiceSupplier(_name, _adapter, _supplier);
	}
	/**
	 * Creates a service supplier default implementation
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _iimplementation must implement
	 * @param _implementation service implementation class
	 * @param _args	Service arguments
	 * @return default ServiceSupplier instance
	 * @see DefaultServiceSupplier#DefaultServiceSupplier(java.lang.String, java.lang.Class, java.lang.Class, java.lang.Object...) 
	 * @since 1.3.0
	 */
	public static <T extends Object> ServiceSupplier from(final String _name,final Class<T> _adapter,final Class<? extends T> _implementation,final Object[] _args){
		return new DefaultServiceSupplier(_name, _adapter, _implementation, _args);
	}
	/**
	 * Constructor of service supplier
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _iimplementation must implement
	 * @param _isSingleton  singleton flag
	 * @param _supplier adapter class implementation supplier
	 * @return default ServiceSupplier instance
	 * @see DefaultServiceSupplier#DefaultServiceSupplier(java.lang.String, java.lang.Class, boolean, java.util.function.Supplier) 
	 * @since 1.3.0
	 */
	public static <T extends Object> ServiceSupplier from(final String _name,final Class<T> _adapter,final boolean _isSingleton,final Supplier<? extends T> _supplier){
		return new DefaultServiceSupplier(_name, _adapter, _adapter, _isSingleton, _supplier);
	}
	/**
	 * Constructor of service supplier
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _implementation must implement
	 * @param _implementation implementation of the _adapter (optional)
	 * @param _isSingleton  singleton flag
	 * @param _supplier adapter class implementation supplier
	 * @return default ServiceSupplier instance
	 * @see DefaultServiceSupplier#DefaultServiceSupplier(java.lang.String, java.lang.Class, java.lang.Class, boolean, java.util.function.Supplier) 
	 * @since 1.3.0
	 */
	public static <T extends Object> ServiceSupplier from(final String _name,final Class<T> _adapter,final Class<? extends T> _implementation,final boolean _isSingleton,final Supplier<? extends T> _supplier){
		return new DefaultServiceSupplier(_name, _adapter, _implementation,_isSingleton,_supplier);
	}
	/**
	 * Constructor of service supplier
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _iimplementation must implement
	 * @param _isSingleton  singleton flag
	 * @param _implementation service implementation class
	 * @param _args	Service arguments
	 * @return default ServiceSupplier instance
	 * @see DefaultServiceSupplier#DefaultServiceSupplier(java.lang.String, java.lang.Class, boolean, java.lang.Class, java.lang.Object...) 
	 * @since 1.3.0
	 */
	public static <T extends Object> ServiceSupplier from(final String _name,final Class<T> _adapter,final boolean _isSingleton,final Class<? extends T> _implementation,final Object[] _args){
		return new DefaultServiceSupplier(_name, _adapter, _isSingleton, _implementation, _args);
	}
}

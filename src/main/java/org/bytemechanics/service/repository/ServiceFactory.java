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

import java.text.MessageFormat;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bytemechanics.service.repository.exceptions.ServiceDisposeException;
import org.bytemechanics.service.repository.exceptions.ServiceInitializationException;

/**
 * Interface to implement for any service supplier, tipically an enum<br>
 * The minimum format should be:<br>
 * <code>
 * enum MyServiceFactory implements ServiceFactory{<br>
 * &nbsp;&nbsp;&nbsp;MYSERVICE(ServiceInterface.class,ServiceImplementation.class),<br>
 * &nbsp;&nbsp;&nbsp;;<br>
 * &nbsp;&nbsp;&nbsp;<br>
 * &nbsp;&nbsp;&nbsp;private final ServiceSupplier serviceSupplier;	<br>
 * &nbsp;&nbsp;&nbsp;<br>
 * &nbsp;&nbsp;&nbsp;&lt;T&gt; MyServiceFactory(final Class&lt;T&gt; _adapter,final Class&lt;? extends T&gt; _implementation,final Object... _args){<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.serviceSupplier=new DefaultServiceSupplier(name(), _adapter, _implementation,_args);<br>
 * &nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;&nbsp;<br>
 * &nbsp;&nbsp;&nbsp;@Override<br>
 * &nbsp;&nbsp;&nbsp;public ServiceSupplier getServiceSupplier() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return this.serviceSupplier;<br>
 * &nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;&nbsp;<br>	
 * &nbsp;&nbsp;&nbsp;public static final void startup(){<br>
 * &nbsp;&nbsp;&nbsp;	ServiceFactory.startup(Stream.of(MyServiceFactory.values()));<br>
 * &nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;&nbsp;public static final void shutdown(){<br>
 * &nbsp;&nbsp;&nbsp;	ServiceFactory.shutdown(Stream.of(MyServiceFactory.values()));<br>
 * &nbsp;&nbsp;&nbsp;}<br>
 * &nbsp;&nbsp;&nbsp;public static final void reset(){<br>
 * &nbsp;&nbsp;&nbsp;	ServiceFactory.reset(Stream.of(MyServiceFactory.values()));<br>
 * &nbsp;&nbsp;&nbsp;}<br>
 * }
 * </code>
 * @author afarre
 * @since 0.1.0
 */
public interface ServiceFactory {
	
	/**
	 * Method to return the internal method supplier
	 * @return ServiceSupplier
	 * @see ServiceSupplier
	 */
	public ServiceSupplier getServiceSupplier();
	
	/**
	 * Method to return the service name, the name is retrieved from the service supplier
	 * @return Service name
	 */
	public default String name(){
		return getServiceSupplier().getName();
	}
	/**
	 * Method to return the service adapter interface that any implementation must accomplish in order to be an eligible implementation
	 * @return Service adapter interface
	 */
	public default Class getAdapter(){
		return getServiceSupplier().getAdapter();
	}
	/**
	 * Method to return if the service must be instantiated as singleton (only on instance in all virtual machine)
	 * @return true if the this service is a singleton
	 */
	public default boolean isSingleton(){
		return getServiceSupplier().isSingleton();
	}
	/**
	 * Method to obtain the service instance, note that if the service is a singleton always returns the same instance. This instance is obtained by calling ServiceSupplie#get()
	 * @return the service instance as Object or null if the instance can not be obtained
	 * @see ServiceSupplier#get() 
	 */
	public default Object get(){
		return getServiceSupplier().get();
	}
	/**
	 * Method to obtain the service instance casted to the given _class, note that if the service is a singleton always returns the same instance. This instance is obtained by calling #get()
	 * @param <T> type of the interface to implement
	 * @param _class class to cast when service is returned
	 * @return the service instance cast to _class
	 * @see #get() 
	 */
	public default <T> T get(final Class<T> _class){
		return (T)get();
	}
	/**
	 * Method to obtain the service instance, note that if the service is a singleton always returns the same instance. This instance is obtained by calling ServiceSupplie#tryGet()
	 * @return the optional service instance as Object that can be empty if the instance can not be obtained
	 * @see ServiceSupplier#tryGet() 
	 */
	public default Optional<Object> tryGet(){
		return getServiceSupplier().tryGet();
	}
	/**
	 * Method to obtain the service instance casted to the given _class, note that if the service is a singleton always returns the same instance. This instance is obtained by calling #tryGet()
	 * @param <T> type of the interface to implement
	 * @param _class class to cast when service is returned
	 * @return the optional service instance as Object that can be empty if the instance can not be obtained
	 * @see #tryGet() 
	 */
	public default <T> Optional<T> tryGet(final Class<T> _class){
		return tryGet()
				.map(object -> (T)object);
	}
	/**
	 * Service initialization; invoque ServiceSupplier#init().
	 * @throws ServiceInitializationException when service can not be instantiated
	 * @see ServiceSupplier#init() 
	 */
	public default void init(){

		final Logger logger=Logger.getLogger(ServiceFactory.class.getName());

		try{
			logger.finest(() -> MessageFormat.format("service::factory::init::{0}::begin",name()));
			getServiceSupplier().init();
			logger.finest(() -> MessageFormat.format("service::factory::init::{0}::end",name()));
		}catch(ServiceInitializationException e){
			logger.log(Level.SEVERE,e,() -> MessageFormat.format("service::factory::init::{0}::fail::{1}",name(),e.getMessage()));
			throw e;
		}catch(RuntimeException e){
			logger.log(Level.SEVERE,e,() -> MessageFormat.format("service::factory::init::{0}::fail::{1}",name(),e.getMessage()));
			throw new ServiceInitializationException(name(),e.getMessage(),e);
		}
	}
	/**
	 * Service dispose; invoque ServiceSupplier#dispose().
	 * @throws ServiceDisposeException when service can not be disposed
	 * @see ServiceSupplier#dispose() 
	 */
	public default void dispose(){
		
		final Logger logger=Logger.getLogger(ServiceFactory.class.getName());

		try{
			logger.finest(() -> MessageFormat.format("service::factory::dispose::{0}::begin",name()));
			getServiceSupplier().dispose();
			logger.finest(() -> MessageFormat.format("service::factory::dispose::{0}::end",name()));
		}catch(ServiceDisposeException e){
			logger.log(Level.SEVERE,e,() -> MessageFormat.format("service::factory::dispose::{0}::fail::{1}",name(),e.getMessage()));
			throw e;
		}catch(Throwable e){
			logger.log(Level.SEVERE,e,() -> MessageFormat.format("service::factory::dispose::{0}::fail::{1}",name(),e.getMessage()));
			throw new ServiceDisposeException(name(),e.getMessage(),e);
		}
	}

	
	/**
	 * Utility method to invoke init() method to all serviceFactories of the stream
	 * @param _services Stream of ServiceFactory instances to initialize
	 * @throws ServiceInitializationException when service can not be initialized
	 * @see #init() 
	 */
	public static void startup(final Stream<ServiceFactory> _services){

		final Logger logger=Logger.getLogger(ServiceFactory.class.getName());

		logger.finest("service::factory::startup::begin");
		_services.forEach(service -> service.init());
		logger.finer("service::factory::startup::end");
	}
	/**
	 * Utility method to invoke dispose() method to all serviceFactories of the stream
	 * @param _services Stream of ServiceFactory instances to dispose
	 * @throws ServiceDisposeException when service can not be disposed
	 * @see #dispose() 
	 */
	public static void shutdown(final Stream<ServiceFactory> _services){

		final Logger logger=Logger.getLogger(ServiceFactory.class.getName());

		logger.finest("service::factory::close::begin");
		_services.forEach(service -> service.dispose());
		logger.finest("service::factory::close::end");
	}
	/**
	 * Utility method to invoke dispose() and init() methods to all serviceFactories of the stream in order to reset it's instances
	 * @param _services Stream of ServiceFactory instances to reset
	 * @throws ServiceInitializationException when service can not be initialized
	 * @throws ServiceDisposeException when service can not be disposed
	 * @see #init() 
	 * @see #dispose() 
	 */
	public static void reset(final Stream<ServiceFactory> _services){

		final Logger logger=Logger.getLogger(ServiceFactory.class.getName());

		logger.finest("service::factory::reset::begin");
		_services.forEach(service -> {
										service.dispose();
										service.init();
									});
		logger.finer("service::factory::reset::end");
	}	
}

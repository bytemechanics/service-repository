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
package org.bytemechanics.service.repository.beans;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bytemechanics.service.repository.ServiceSupplier;
import org.bytemechanics.service.repository.exceptions.ServiceInitializationException;
import org.bytemechanics.service.repository.exceptions.UnableToSetInstanceException;

/**
 * Default Service supplier implementation
 * @author afarre
 * @since 0.1.0
 * @version 1.1.0
 * @see ServiceSupplier
 */
public class DefaultServiceSupplier implements ServiceSupplier{

	private final String name;
	private final Supplier originalSupplier;
	private Supplier supplier;
	private final Consumer disposeConsumer;
	private final Class adapter;
	private final Class implementation;
	private final boolean singleton;
	private volatile Object instance;
	
	
	/**
	 * Constructor of service supplier
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _implementation must implement
	 * @param _isSingleton  singleton flag
	 * @param _supplier adapter class implementation supplier
	 * @param _disposeConsumer dispose consumer
	 * @param _implementation implementation of the _adapter (optional)
	 * @param _args arguments to use with the implementation if necessary
	 * @since 1.2.0
	 */
	public <T> DefaultServiceSupplier(final String _name,final Class<T> _adapter,final boolean _isSingleton,final Supplier<? extends T> _supplier,final Consumer<? extends T> _disposeConsumer,final Class<? extends T> _implementation,final Object... _args){
		this.name=_name;
		this.adapter=_adapter;
		this.singleton=_isSingleton;
		this.supplier=_supplier;
		this.implementation=_implementation;
		if(this.supplier==null){
			if(_implementation==null){
				throw new ServiceInitializationException(_name,"Unable to create service supplier without supplier or implementation");
			}
			this.supplier=ServiceSupplier.generateSupplier(this.name,this.implementation,_args);
		}
		this.originalSupplier=this.supplier;
		this.disposeConsumer=(_disposeConsumer!=null)? _disposeConsumer : ServiceSupplier.generateConsumer(this.name);
		this.instance=null;
	}

	/**
	 * @return name
	 * @see ServiceSupplier#getName() 
	 */	
	@Override
	public String getName() {
		return name;
	}
	/**
	 * @return adaper class
	 * @see ServiceSupplier#getAdapter()  
	 */	
	@Override
	public Class getAdapter() {
		return adapter;
	}
	/**
	 * @return implementation class
	 * @see ServiceSupplier#getImplementation() 
	 */	
	@Override
	public Class getImplementation() {
		return implementation;
	}
	/**
	 * @return singleton indicator flag
	 * @see ServiceSupplier#isSingleton()
	 */	
	@Override
	public boolean isSingleton() {
		return singleton;
	}
	/**
	 * @return supplier
	 * @see ServiceSupplier#getSupplier() 
	 */	
	@Override
	public Supplier getSupplier() {
		return supplier;
	}
	/**
	 * @see ServiceSupplier#setSupplier(java.util.function.Supplier) 
	 * @since 1.2.0
	 */	
	@Override
	public void setSupplier(final Supplier _supplier) {
		this.supplier=_supplier;
	}
	/**
	 * @see ServiceSupplier#getDisposeConsumer() 
	 * @since 1.3.0
	 */
	@Override
	public Consumer getDisposeConsumer(){
		return this.disposeConsumer;
	}

	/**
	 * @see ServiceSupplier#reset() 
	 * @since 1.3.0
	 */	
	@Override
	public void reset() {
		this.supplier=this.originalSupplier;
		this.instance=null;
	}

	/**
	 * @return current service instance or null
	 * @see ServiceSupplier#getInstance() 
	 */	
	@Override
	public Object getInstance() {
		return instance;
	}
	/**
	 * @param _instance instance to store
	 * @see ServiceSupplier#getName() 
	 */	
	@Override
	public void setInstance(final Object _instance) {
		
		if((_instance!=null)&&(!getAdapter().isAssignableFrom(_instance.getClass()))){
			throw new UnableToSetInstanceException(_instance,getAdapter());
		}
		this.instance = _instance;
	}
	
	/**
	 * Helper class to instantiate ServiceSupplier
	 * @param <TYPE> type of the adapter to build supplier
	 * @since 1.3.0
	 */
	public static class DefaultServiceSupplierBuilder<TYPE>{
		
		private String name;
		private Supplier<? extends TYPE> supplier;
		private final Class<TYPE> adapter;
		private Class<? extends TYPE> implementation;
		private boolean singleton;
		private Consumer<? extends TYPE> disposeConsumer;
		private Object[] args;
	
		public DefaultServiceSupplierBuilder(final Class<TYPE> _adapter){
			this.name=null;
			this.supplier=null;
			this.adapter=_adapter;
			this.implementation=null;
			this.singleton=false;
			this.args=new Object[0];
			this.disposeConsumer=null;
		}
		
		/**
		 * Sets the name to the builder
		 * @param _name value
		 * @return DefaultServiceSupplierBuilder
		 */
		public DefaultServiceSupplierBuilder<TYPE> name(final String _name) {
			this.name = _name;
			return this;
		}
		/**
		 * Sets the supplier to the builder
		 * @param _supplier value
		 * @return DefaultServiceSupplierBuilder
		 */
		public DefaultServiceSupplierBuilder<TYPE> supplier(final Supplier<? extends TYPE> _supplier) {
			this.supplier = _supplier;
			return this;
		}
		/**
		 * Sets the disposeConsumer to the builder
		 * @param _disposeConsumer value
		 * @return DefaultServiceSupplierBuilder
		 */
		public DefaultServiceSupplierBuilder<TYPE> disposeConsumer(final Consumer<? extends TYPE> _disposeConsumer) {
			this.disposeConsumer = _disposeConsumer;
			return this;
		}
		
		/**
		 * Sets the arguments to the builder
		 * @param _arguments arguments to use to create a supplier from implementation
		 * @return DefaultServiceSupplierBuilder
		 */
		public DefaultServiceSupplierBuilder<TYPE> args(final Object... _arguments) {
			this.args = _arguments;
			return this;
		}
		/**
		 * Sets the implementation to the builder
		 * @param _implementation value
		 * @return DefaultServiceSupplierBuilder
		 */
		public DefaultServiceSupplierBuilder<TYPE> implementation(final Class<? extends TYPE> _implementation) {
			this.implementation = _implementation;
			return this;
		}
		/**
		 * Sets the singleton to the builder
		 * @param _singleton value
		 * @return DefaultServiceSupplierBuilder
		 */
		public DefaultServiceSupplierBuilder<TYPE> singleton(final boolean _singleton) {
			this.singleton = _singleton;
			return this;
		}
		
		/**
		 * Create the DefaultServiceSupplier instance configured with the builder values
		 * @return DefaultServiceSupplier
		 */
		public DefaultServiceSupplier build() {
			return new DefaultServiceSupplier(name, adapter, singleton, supplier,disposeConsumer,implementation,args);
		}
	}

	/**
	 * Instantiate builder for DefaultServiceSupplier
	 * @param <T> type of the default service supplier
	 * @param _adapter adapter class
	 * @return a builder for DefaultServiceSupplier
	 * @see DefaultServiceSupplierBuilder
	 * @since 1.3.0
	 */
	@java.lang.SuppressWarnings("all")
	public static <T> DefaultServiceSupplierBuilder<T> builder(final Class<T> _adapter) {
		return new DefaultServiceSupplierBuilder<>(_adapter);
	}	
}

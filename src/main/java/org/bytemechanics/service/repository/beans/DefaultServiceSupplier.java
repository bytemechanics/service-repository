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

import java.util.function.Supplier;
import org.bytemechanics.service.repository.ServiceSupplier;
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
	private final Class adapter;
	private final Class implementation;
	private final boolean singleton;
	private volatile Object instance;
	
	
	/**
	 * Constructor of service supplier
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _iimplementation must implement
	 * @param _implementation service implementation class
	 * @param _args	Service arguments
	 */
	public <T> DefaultServiceSupplier(final String _name,final Class<T> _adapter,final Class<? extends T> _implementation,final Object... _args){
		this(_name,_adapter,_implementation,false,ServiceSupplier.generateSupplier(_name,_implementation,_args));
	}
	/**
	 * Constructor of service supplier
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _iimplementation must implement
	 * @param _supplier adapter class implementation supplier
	 */
	public <T> DefaultServiceSupplier(final String _name,final Class<T> _adapter,final Supplier<? extends T> _supplier){
		this(_name,_adapter,null,false,_supplier);
	}
	/**
	 * Constructor of service supplier
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _iimplementation must implement
	 * @param _isSingleton  singleton flag
	 * @param _implementation service implementation class
	 * @param _args	Service arguments
	 */
	public <T> DefaultServiceSupplier(final String _name,final Class<T> _adapter,final boolean _isSingleton,final Class<? extends T> _implementation,final Object... _args){
		this(_name,_adapter,_implementation,_isSingleton,ServiceSupplier.generateSupplier(_name,_implementation,_args));
	}
	/**
	 * Constructor of service supplier
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _iimplementation must implement
	 * @param _isSingleton  singleton flag
	 * @param _supplier adapter class implementation supplier
	 */
	public <T> DefaultServiceSupplier(final String _name,final Class<T> _adapter,final boolean _isSingleton,final Supplier<? extends T> _supplier){
		this(_name,_adapter,null,_isSingleton,_supplier);
	}
	/**
	 * Constructor of service supplier
	 * @param <T> adapter class type
	 * @param _name Service name
	 * @param _adapter interface class that _implementation must implement
	 * @param _implementation implementation of the _adapter (optional)
	 * @param _isSingleton  singleton flag
	 * @param _supplier adapter class implementation supplier
	 * @since 1.2.0
	 */
	public <T> DefaultServiceSupplier(final String _name,final Class<T> _adapter,final Class<? extends T> _implementation,final boolean _isSingleton,final Supplier<? extends T> _supplier){
		this.name=_name;
		this.adapter=_adapter;
		this.singleton=_isSingleton;
		this.originalSupplier=_supplier;
		this.supplier=this.originalSupplier;
		this.instance=null;
		this.implementation=_implementation;
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
}

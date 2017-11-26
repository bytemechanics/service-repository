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

import java.text.MessageFormat;
import java.util.function.Supplier;
import org.bytemechanics.service.repository.ServiceSupplier;

/**
 * Default Service supplier implementation
 * @author afarre
 * @since 0.1.0
 * @see ServiceSupplier
 */
public class DefaultServiceSupplier implements ServiceSupplier{

	private final String name;
	private final Supplier supplier;
	private final Class adapter;
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
		this(_name,_adapter,false,_implementation);
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
		this(_name,_adapter,false,ServiceSupplier.generateSupplier(_name,_implementation,_args));
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
		this.name=_name;
		this.adapter=_adapter;
		this.singleton=_isSingleton;
		this.supplier=_supplier;
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
			throw new RuntimeException(MessageFormat.format("Unable to set instance {0} that doesn't implement the required adapter {1}",_instance,getAdapter()));
		}
		this.instance = _instance;
	}
}

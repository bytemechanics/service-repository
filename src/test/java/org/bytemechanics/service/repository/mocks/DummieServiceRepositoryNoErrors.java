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
package org.bytemechanics.service.repository.mocks;

import java.util.function.Supplier;
import java.util.stream.Stream;
import org.bytemechanics.service.repository.ServiceRepository;
import org.bytemechanics.service.repository.ServiceSupplier;
import org.bytemechanics.service.repository.beans.DefaultServiceSupplier;

/**
 *
 * @author afarre
 */
public enum DummieServiceRepositoryNoErrors implements ServiceRepository {

	DUMMIE_SERVICE_0ARG(DummieService.class,DummieServiceImpl.class),
	DUMMIE_SERVICE_1ARG(DummieService.class,DummieServiceImpl.class,"1arg-arg1"),
	DUMMIE_SERVICE_3ARG(DummieService.class,DummieServiceImpl.class,"3arg-arg1",3,"3arg-arg2"),
	DUMMIE_SERVICE_SUPPLIER_0ARG(DummieService.class,() -> new DummieServiceImpl()),
	DUMMIE_SERVICE_SUPPLIER_1ARG(DummieService.class,() -> new DummieServiceImpl("1arg-arg1")),
	DUMMIE_SERVICE_SUPPLIER_3ARG(DummieService.class,() -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")),
	SINGLETON_DUMMIE_SERVICE_0ARG(DummieService.class,true,DummieServiceImpl.class),
	SINGLETON_DUMMIE_SERVICE_1ARG(DummieService.class,true,DummieServiceImpl.class,"1arg-arg1"),
	SINGLETON_DUMMIE_SERVICE_3ARG(DummieService.class,true,DummieServiceImpl.class,"3arg-arg1",3,"3arg-arg2"),
	SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG(DummieService.class,true,() -> new DummieServiceImpl()),
	SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG(DummieService.class,true,() -> new DummieServiceImpl("1arg-arg1")),
	SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG(DummieService.class,true,() -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")),
	;

	private final ServiceSupplier serviceSupplier;	
		
	
	<T> DummieServiceRepositoryNoErrors(final Class<T> _adapter,final Class<? extends T> _implementation,final Object... _args){
		this.serviceSupplier=new DefaultServiceSupplier(name(), _adapter, _implementation,_args);
	}
	<T> DummieServiceRepositoryNoErrors(final Class<T> _adapter,final Supplier<? extends T> _implementationSupplier){
		this.serviceSupplier=new DefaultServiceSupplier(name(), _adapter, _implementationSupplier);
	}
	<T> DummieServiceRepositoryNoErrors(final Class<T> _adapter,final boolean _singleton,final Class<? extends T> _implementation,final Object... _args){
		this.serviceSupplier=new DefaultServiceSupplier(name(),_adapter,_singleton,_implementation,_args);
	}
	<T> DummieServiceRepositoryNoErrors(final Class<T> _adapter,final boolean _singleton,final Supplier<? extends T> _implementationSupplier){
		this.serviceSupplier=new DefaultServiceSupplier(name(),_adapter,_singleton,_implementationSupplier);
	}
		
	@Override
	public ServiceSupplier getServiceSupplier() {
		return this.serviceSupplier;
	}

	
	public static final void startup(){
		ServiceRepository.startup(Stream.of(DummieServiceRepositoryNoErrors.values()));
	}
	public static final void shutdown(){
		ServiceRepository.shutdown(Stream.of(DummieServiceRepositoryNoErrors.values()));
	}
	public static final void reset(){
		ServiceRepository.reset(Stream.of(DummieServiceRepositoryNoErrors.values()));
	}
}

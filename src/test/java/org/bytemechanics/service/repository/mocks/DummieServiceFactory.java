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

import java.util.stream.Stream;
import org.bytemechanics.service.repository.ServiceFactory;
import org.bytemechanics.service.repository.ServiceSupplier;
import org.bytemechanics.service.repository.beans.DefaultServiceSupplier;

/**
 *
 * @author afarre
 */
public enum DummieServiceFactory implements ServiceFactory {

	DUMMIE_SERVICE_0ARG(DummieService.class,DummieServiceImpl.class),
	DUMMIE_SERVICE_1ARG(DummieService.class,DummieServiceImpl.class,"1arg-arg1"),
	DUMMIE_SERVICE_3ARG(DummieService.class,DummieServiceImpl.class,"3arg-arg1",3,"3arg-arg2"),
	DUMMIE_SERVICE_4ARG(DummieService.class,DummieServiceImpl.class,"4arg-arg1",4,"4arg-arg2",true),
	SINGLETON_DUMMIE_SERVICE_0ARG(DummieService.class,true,DummieServiceImpl.class),
	SINGLETON_DUMMIE_SERVICE_1ARG(DummieService.class,true,DummieServiceImpl.class,"1arg-arg1"),
	SINGLETON_DUMMIE_SERVICE_3ARG(DummieService.class,true,DummieServiceImpl.class,"3arg-arg1",3,"3arg-arg2"),
	SINGLETON_DUMMIE_SERVICE_4ARG(DummieService.class,true,DummieServiceImpl.class,"4arg-arg1",4,"4arg-arg2",true),
	;

	private final ServiceSupplier serviceSupplier;	
		
	
	<T> DummieServiceFactory(final Class<T> _adapter,final Class<? extends T> _implementation,final Object... _args){
		this.serviceSupplier=new DefaultServiceSupplier(name(), _adapter, _implementation,_args);
	}
	<T> DummieServiceFactory(final Class<T> _adapter,final boolean _singleton,final Class<? extends T> _implementation,final Object... _args){
		this.serviceSupplier=new DefaultServiceSupplier(name(),_adapter,_singleton,_implementation,_args);
	}
		
	@Override
	public ServiceSupplier getServiceSupplier() {
		return this.serviceSupplier;
	}

	
	public static final void startup(){
		ServiceFactory.startup(Stream.of(DummieServiceFactory.values()));
	}
	public static final void shutdown(){
		ServiceFactory.shutdown(Stream.of(DummieServiceFactory.values()));
	}
	public static final void reset(){
		ServiceFactory.reset(Stream.of(DummieServiceFactory.values()));
	}
}

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

package org.bytemechanics.service.repository
import org.bytemechanics.service.repository.mocks.DummieServiceRepository
import org.bytemechanics.service.repository.mocks.DummieServiceRepositoryNoErrors
import org.bytemechanics.service.repository.mocks.DummieService
import org.bytemechanics.service.repository.mocks.DummieServiceImpl
import org.bytemechanics.service.repository.exceptions.ServiceInitializationException
import java.text.MessageFormat
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author afarre
 */
class ServiceRepositorySpec extends Specification{
	
	@Unroll
	def "Service repository must return a valid #serviceAdapter instance when call get to the corresponding #service.name of service repository"(){
		when:
			def instance=service.get()
			
		then:
			instance!=null
			service.getAdapter().isAssignableFrom(instance.getClass())
			
		where:
			service															| serviceAdapter
			DummieServiceRepository.DUMMIE_SERVICE_0ARG						| DummieService.class
			DummieServiceRepository.DUMMIE_SERVICE_1ARG						| DummieService.class
			DummieServiceRepository.DUMMIE_SERVICE_3ARG						| DummieService.class
			DummieServiceRepository.DUMMIE_SERVICE_SUPPLIER_0ARG			| DummieService.class
			DummieServiceRepository.DUMMIE_SERVICE_SUPPLIER_1ARG			| DummieService.class
			DummieServiceRepository.DUMMIE_SERVICE_SUPPLIER_3ARG			| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_0ARG			| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_1ARG			| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_3ARG			| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG	| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG	| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG	| DummieService.class
	}

	@Unroll
	def "Service repository must return a valid Optional of #service.adapter instance when call tryget to the corresponding #service.name of service repository"(){
		when:
			def optionalInstance=service.tryGet()
			
		then:
			optionalInstance!=null
			optionalInstance instanceof Optional
			service.getAdapter().isAssignableFrom(optionalInstance.get().getClass())
			
		where:
			service															| serviceAdapter
			DummieServiceRepository.DUMMIE_SERVICE_0ARG						| DummieService.class
			DummieServiceRepository.DUMMIE_SERVICE_1ARG						| DummieService.class
			DummieServiceRepository.DUMMIE_SERVICE_3ARG						| DummieService.class
			DummieServiceRepository.DUMMIE_SERVICE_SUPPLIER_0ARG			| DummieService.class
			DummieServiceRepository.DUMMIE_SERVICE_SUPPLIER_1ARG			| DummieService.class
			DummieServiceRepository.DUMMIE_SERVICE_SUPPLIER_3ARG			| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_0ARG			| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_1ARG			| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_3ARG			| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG	| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG	| DummieService.class
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG	| DummieService.class
	}

	@Unroll
	def "Service repository must return #same #service.adapter #type instance when call get"(){
		
		when:
			def instance1=service.get(serviceAdapter)
			def instance2=service.get()
			
		then:
			instance1!=null
			instance2!=null
			if(type=="same"){
				instance1==instance2
			}else{
				instance1!=instance2
			}
			
		where:
			service															| serviceAdapter		| type
			DummieServiceRepository.DUMMIE_SERVICE_0ARG						| DummieService.class	| ""
			DummieServiceRepository.DUMMIE_SERVICE_1ARG						| DummieService.class	| ""
			DummieServiceRepository.DUMMIE_SERVICE_3ARG						| DummieService.class	| ""
			DummieServiceRepository.DUMMIE_SERVICE_SUPPLIER_0ARG			| DummieService.class	| ""
			DummieServiceRepository.DUMMIE_SERVICE_SUPPLIER_1ARG			| DummieService.class	| ""
			DummieServiceRepository.DUMMIE_SERVICE_SUPPLIER_3ARG			| DummieService.class	| ""
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_0ARG			| DummieService.class	| "singleton"
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_1ARG			| DummieService.class	| "singleton"
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_3ARG			| DummieService.class	| "singleton"
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG	| DummieService.class	| "singleton"
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG	| DummieService.class	| "singleton"
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG	| DummieService.class	| "singleton"
			
			same=(type=="singleton")? "same" : "distinct"
	}

	@Unroll
	def "Service repository must return #exception when try to instance an inexistent #service constructor "(){
		
		when:
			def instance=service.get()
			
		then:
			def e=thrown(exception)
			e.equals(message)
			
		where:
			service													| exception							
			DummieServiceRepository.DUMMIE_SERVICE_4ARG				| ServiceInitializationException.class	 
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_4ARG	| ServiceInitializationException.class	 
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_4ARG	| ServiceInitializationException.class	 
			DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_4ARG	| ServiceInitializationException.class
			
			message=new ServiceInitializationException(service.name(),MessageFormat
																		.format("Unable to instantiate service with class {0} using constructor({1})", 
																			DummieServiceImpl.class,["4arg-arg1",4,"4arg-arg2",true]))
	}

	@Unroll
	def "Singleton #service should provide a distinct instance once dispose"(){
		
		when:
			def instance1=service.get()
			def instance2=service.get()
			service.dispose()
			def instance3=service.get()
			
		then:
			instance1!=null
			instance2!=null
			instance3!=null
			instance2.isClosed()
			(instance1==instance2)!=instance3
			
		where:
			service	<< [
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_0ARG,
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_1ARG,			
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_3ARG,	
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG,
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG,
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG,		
			]
	}
	
	@Unroll
	def "Singleton #service should initialize instance once called init"(){
		setup:
			service.serviceSupplier.instance=null
																	
		when:
			def instance1=service.getServiceSupplier().getInstance()
			service.init()
			def instance2=service.getServiceSupplier().getInstance()
			
		then:
			instance1==null
			instance2!=null
			
		where:
			service	<< [
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_0ARG,
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_1ARG,			
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_3ARG,	
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG,
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG,
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG,		
			]
	}
	
	@Unroll
	def "Singleton #service instance should be able to be replaced with #instance"(){
		setup:
			DummieServiceRepositoryNoErrors.startup()
																	
		when:
			def initialInstance=service.get()
			service.getServiceSupplier().setInstance(instance);
			def replacedInstance=service.get()
			
		then:
			initialInstance!=null
			replacedInstance!=null
			initialInstance!=replacedInstance
			replacedInstance==instance
			
		where:
			service	<< [
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_0ARG,
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_1ARG,			
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_3ARG,	
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG,
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG,
				DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG,		
			]
			instance=new DummieServiceImpl();
	}
	
	@Unroll
	def "Service repository should initialize all services on startup call"(){
		setup:
			for(DummieServiceRepositoryNoErrors service:DummieServiceRepositoryNoErrors.values()){
				service.serviceSupplier.instance=null
			}
																	
		when:
			DummieServiceRepositoryNoErrors.startup()
			
		then:
			for(DummieServiceRepositoryNoErrors service:DummieServiceRepositoryNoErrors.values()){
				if(service.isSingleton()){
					service.serviceSupplier.instance!=null
				}
			}
	}
	@Unroll
	def "Service repository should dispose all services on shutdown call"(){
		setup:
			for(DummieServiceRepositoryNoErrors service:DummieServiceRepositoryNoErrors.values()){
				if(service.isSingleton()){
					service.init()
				}
			}
																	
		when:
			DummieServiceRepositoryNoErrors.shutdown()
			
		then:
			for(DummieServiceRepositoryNoErrors service:DummieServiceRepositoryNoErrors.values()){
				if(service.isSingleton()){
					service.serviceSupplier.instance==null
				}
			}
	}
	@Unroll
	def "Service repository should reinstance all services on reset call"(){
		setup:
			def Map instances=new HashMap();
			for(DummieServiceRepositoryNoErrors service:DummieServiceRepositoryNoErrors.values()){
				if(service.isSingleton()){
					service.init()
					instances.put(service.name(),service.serviceSupplier.instance);
					
				}
			}
																	
		when:
			DummieServiceRepositoryNoErrors.reset()
			
		then:
			for(DummieServiceRepositoryNoErrors service:DummieServiceRepositoryNoErrors.values()){
				if(service.isSingleton()){
					service.serviceSupplier.instance!=instances.get(service.name())
				}
			}
	}
}


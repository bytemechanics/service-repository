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
import org.bytemechanics.service.repository.mocks.DummieServiceRepositoryTypeSave
import org.bytemechanics.service.repository.exceptions.ServiceInitializationException
import org.bytemechanics.service.repository.exceptions.ServiceDisposeException
import java.text.MessageFormat
import spock.lang.Specification
import spock.lang.Unroll
import java.util.logging.*
import java.util.stream.*


/**
 * @author afarre
 */
class ServiceRepositorySpec extends Specification{
	
	def setupSpec(){
		println(">>>>> ServiceRepositorySpec >>>> setupSpec")
		final InputStream inputStream = ServiceRepositorySpec.class.getResourceAsStream("/logging.properties");
		try{
			LogManager.getLogManager().readConfiguration(inputStream);
		}catch (final IOException e){
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}finally{
			if(inputStream!=null)
				inputStream.close();
		}
	}
	
	@Unroll
	def "Service repository #service must return a valid #service.adapter instance when call get"(){
		println(">>>>> ServiceRepositorySpec >>>> Service repository $service must return a valid $service.adapter instance when call get")

		when:
			def instance=service.get()
			
		then:
			instance!=null
			service.getAdapter().isAssignableFrom(instance.getClass())
			
		where:
			service	<< 	DummieServiceRepositoryNoErrors.values()
	}
	
	@Unroll
	def "Service repository must return an #exception when can not instance #service instance"(){
		println(">>>>> ServiceRepositorySpec >>>> Service repository must return an $exception when can not instance $service instance")

		when:
			def instance=service.get()
			
		then:
			thrown(exception)
			
		where:
			service	<< [DummieServiceRepository.DUMMIE_SERVICE_4ARG,DummieServiceRepository.SINGLETON_DUMMIE_SERVICE_4ARG]
			exception=ServiceInitializationException.class
	}

	@Unroll
	def "Service repository #service must return a valid Optional of #service.adapter instance when call tryget"(){
		println(">>>>> ServiceRepositorySpec >>>> Service repository $service must return a valid Optional of $service.adapter instance when call tryget")

		when:
			def optionalInstance=service.tryGet()
			
		then:
			optionalInstance!=null
			optionalInstance instanceof Optional
			service.getAdapter().isAssignableFrom(optionalInstance.get().getClass())
			
		where:
			service << DummieServiceRepositoryNoErrors.values()
	}

	@Unroll
	def "Service repository #service must return the same #serviceAdapter instance when call get on singletons"(){
		println(">>>>> ServiceRepositorySpec >>>> Service repository $service must return the same $serviceAdapter instance when call get on singletons")
		
		when:
			def instance1=service.get(serviceAdapter)
			def instance2=service.get()
			
		then:
			instance1!=null
			instance2!=null
			instance1==instance2
			
		where:
			service << Stream.of(DummieServiceRepositoryNoErrors.values())
										.filter({serviceRepo -> serviceRepo.isSingleton()})
										.collect(Collectors.toList())
			serviceAdapter=DummieService.class
	}

	@Unroll
	def "Service repository #service must return distinct #serviceAdapter instance when call get on singletons"(){
		println(">>>>> ServiceRepositorySpec >>>> Service repository $service must return distinct $serviceAdapter instance when call get on singletons")
		
		when:
			def instance1=service.get(serviceAdapter)
			def instance2=service.get()
			
		then:
			instance1!=null
			instance2!=null
			instance1!=instance2
			
		where:
			service << Stream.of(DummieServiceRepositoryNoErrors.values())
										.filter({serviceRepo -> !serviceRepo.isSingleton()})
										.collect(Collectors.toList())
			serviceAdapter=DummieService.class
	}

	@Unroll
	def "Service repository must return #exception when try to instance an inexistent #service constructor"(){
		println(">>>>> ServiceRepositorySpec >>>> Service repository must return $exception when try to instance an inexistent $service constructor")
		
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
		println(">>>>> ServiceRepositorySpec >>>> Singleton $service should provide a distinct instance once dispose")
		
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
			service << Stream.of(DummieServiceRepositoryNoErrors.values())
										.filter({serviceRepo -> serviceRepo.isSingleton()})
										.collect(Collectors.toList())
	}
	
	@Unroll
	def "Singleton #service should initialize instance once called init"(){
		println(">>>>> ServiceRepositorySpec >>>> Singleton $service should initialize instance once called init")
		
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
			service << Stream.of(DummieServiceRepositoryNoErrors.values())
										.filter({serviceRepo -> serviceRepo.isSingleton()})
										.collect(Collectors.toList())
	}
	
	@Unroll
	def "Singleton #service instance should be able to be replaced with #instance"(){
		println(">>>>> ServiceRepositorySpec >>>> Singleton $service instance should be able to be replaced with $instance")

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
			service << Stream.of(DummieServiceRepositoryNoErrors.values())
										.filter({serviceRepo -> serviceRepo.isSingleton()})
										.collect(Collectors.toList())
			instance=new DummieServiceImpl();
	}
	
	def "Service repository should initialize all services on startup call"(){
		println(">>>>> ServiceRepositorySpec >>>> Service repository should initialize all services on startup call")
		
		setup:
			DummieServiceRepositoryNoErrors.shutdown()
																	
		when:
			DummieServiceRepositoryNoErrors.startup()
					
		then:
			serviceRepository.serviceSupplier.instance!=null

		where:
			serviceRepository << Stream.of(DummieServiceRepositoryNoErrors.values())
										.filter({serviceRepo -> serviceRepo.isSingleton()})
										.collect(Collectors.toList())
	}
	
	def "On shutdown service repository should raise a serviceDisposeException if some service can not be disposed"(){
		println(">>>>> ServiceRepositorySpec >>>> On shutdown service repository should raise a serviceDisposeException if some service can not be disposed")

		setup:
			Stream.of(DummieServiceRepositoryNoErrors.values())
						.filter({serviceRepo -> serviceRepo.isSingleton()})
						.forEach({serviceRepo -> serviceRepo.init()})
			DummieServiceRepositoryNoErrors.SINGLETON_DUMMIE_SERVICE_0ARG
												.get(DummieServiceImpl.class)
												.setFailOnClose(true);
																	
		when:
			DummieServiceRepositoryNoErrors.shutdown()
			
		then:
			Throwable cause=thrown(ServiceDisposeException)
		
		cleanup:
			DummieServiceRepositoryNoErrors.SINGLETON_DUMMIE_SERVICE_0ARG
												.get(DummieServiceImpl.class)
												.setFailOnClose(false);
	}
	
	def "Service repository should dispose all services on shutdown call"(){
		println(">>>>> ServiceRepositorySpec >>>> Service repository should dispose all services on shutdown call")
		
		setup:
			serviceRepository.init()
			def initial=serviceRepository.get()
																	
		when:
			DummieServiceRepositoryNoErrors.shutdown()
					
		then:
			serviceRepository.serviceSupplier.instance==null

		where:
			serviceRepository << DummieServiceRepositoryNoErrors.values()
	}
	
	def "Service repository singletons should be have distinct instance after reset call"(){
		println(">>>>> ServiceRepositorySpec >>>> Service repository singletons should be have distinct instance after reset call")

		setup:
			serviceRepository.init()
			def initial=serviceRepository.get()
																	
		when:
			DummieServiceRepositoryNoErrors.reset()
					
		then:
			serviceRepository.get()==serviceRepository.get()
			serviceRepository.get()!=initial

		where:
			serviceRepository << Stream.of(DummieServiceRepositoryNoErrors.values())
										.filter({serviceRepo -> serviceRepo.isSingleton()})
										.collect(Collectors.toList())
	}
	
	def "Check correct types match in recovery with arguments"(){
		println(">>>>> ServiceRepositorySpec >>>> Check correct types match in recovery with arguments")

		setup:
			DummieServiceRepositoryNoErrors.reset()
			def DummieServiceRepositoryTypeSave test=new DummieServiceRepositoryTypeSave()
			def DummieService testArg0
			def DummieService testArg1
			def DummieService testArg3
			def DummieService testArg0Try
			def DummieService testArg1Try
			def DummieService testArg3Try
												
		when:
			testArg0=test.testArg0()
			testArg1=test.testArg1()
			testArg3=test.testArg3()
			testArg0Try=test.testArg0Try()
			testArg1Try=test.testArg1Try()
			testArg3Try=test.testArg3Try()
		
		then:
			testArg0.getArg1()==""
			testArg0.getArg2()==0
			testArg0.getArg3()==""
			testArg1.getArg1()=="1arg-arg1"
			testArg1.getArg2()==0
			testArg1.getArg3()==""
			testArg3.getArg1()=="3arg-arg1"
			testArg3.getArg2()==3
			testArg3.getArg3()=="3arg-arg2"
			testArg0Try.getArg1()==""
			testArg0Try.getArg2()==0
			testArg0Try.getArg3()==""
			testArg1Try.getArg1()=="1arg-arg1"
			testArg1Try.getArg2()==0
			testArg1Try.getArg3()==""
			testArg3Try.getArg1()=="3arg-arg1"
			testArg3Try.getArg2()==3
			testArg3Try.getArg3()=="3arg-arg2"
	}
}


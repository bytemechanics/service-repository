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

package org.bytemechanics.service.repository.beans

import org.bytemechanics.service.repository.beans.DefaultServiceSupplier;
import org.bytemechanics.service.repository.ServiceSupplier;
import org.bytemechanics.service.repository.exceptions.UnableToSetInstanceException;
import org.bytemechanics.service.repository.mocks.DummieService;
import org.bytemechanics.service.repository.mocks.DummieServiceImpl;
import spock.lang.Specification;
import spock.lang.Unroll
import java.util.logging.*


/**
 * @author afarre
 */
class DefaultServiceSupplierSpec extends Specification{
	
	def setupSpec(){
		println(">>>>> DefaultServiceSupplierSpec >>>> setupSpec")
		final InputStream inputStream = DefaultServiceSupplierSpec.class.getResourceAsStream("/logging.properties");
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
	def "Call DefaultServiceSupplier(#name,#adapter,#singleton,#implementation,#args) should create the #expected instance"(){
		println(">>>>> DefaultServiceSupplierSpec >>>> Call DefaultServiceSupplier($name,$adapter,$singleton,$implementation,$args) should create the $expected  instance")

		when:
			def serviceSupplier=DefaultServiceSupplier.builder(adapter)
															.name(name)
															.singleton(singleton)
															.implementation(implementation)
															.args((Object[])args)
														.build()

		then:
			serviceSupplier!=null
			serviceSupplier.getInstance()==null
			serviceSupplier.getName()==name
			serviceSupplier.getAdapter()==adapter
			serviceSupplier.getSupplier()!=null
			serviceSupplier.getSupplier().get() instanceof DummieServiceImpl
			serviceSupplier.getSupplier().get().getArg1()==expected[0]
			serviceSupplier.getSupplier().get().getArg2()==expected[1]
			serviceSupplier.getSupplier().get().getArg3()==expected[2]
			
		where:
			name										| adapter				| singleton	| implementation			| args
			"DUMMIE_SERVICE_0ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| []
			"DUMMIE_SERVICE_1ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| ["1arg-arg1"]
			"DUMMIE_SERVICE_3ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
			"DUMMIE_SERVICE_SUPPLIER_0ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| []
			"DUMMIE_SERVICE_SUPPLIER_1ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| ["1arg-arg1"]
			"DUMMIE_SERVICE_SUPPLIER_3ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
			"SINGLETON_DUMMIE_SERVICE_0ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| []
			"SINGLETON_DUMMIE_SERVICE_1ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| ["1arg-arg1"]
			"SINGLETON_DUMMIE_SERVICE_3ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| []
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| ["1arg-arg1"]
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]

			expected=[(args.size()>0)? args[0] : "",(args.size()>1)? args[1] : 0,(args.size()>2)? args[2] : ""]			
	}

	@Unroll
	def "Given #name ServiceSupplier instance of #implementation(#constructorArgs) for #adapter as singleton[#singleton] if call get(#getterArgs) we can expect a instance with #expected"(){
		println(">>>>> DefaultServiceSupplierSpec >>>> Given $name ServiceSupplier instance of $implementation($constructorArgs) for $adapter as singleton[$singleton] if call get($getterArgs) we can expect a instance with $expected")

		setup:
			def serviceSupplier=DefaultServiceSupplier.builder(adapter)
															.name(name)
															.singleton(singleton)
															.implementation(implementation)
															.args((Object[])constructorArgs)
														.build()
			def instance
		
		when:
			serviceSupplier.init()
			instance=serviceSupplier.get((Object[])getterArgs)

		then:
			instance!=null
			instance.getArg1()==expected[0]
			instance.getArg2()==expected[1]
			instance.getArg3()==expected[2]
			
		where:
			name										| adapter				| singleton	| implementation			| constructorArgs				| getterArgs													
			"DUMMIE_SERVICE_0ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| []							| ["3arg-arg1-alt",4,"3arg-arg2-alt"]
			"DUMMIE_SERVICE_1ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| ["1arg-arg1"]					| []												
			"DUMMIE_SERVICE_3ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]	| ["1arg-arg1-alt"]									
			"DUMMIE_SERVICE_SUPPLIER_0ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| []							| ["3arg-arg1-alt",4,"3arg-arg2-alt"]
			"DUMMIE_SERVICE_SUPPLIER_1ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| ["1arg-arg1"]					| []												
			"DUMMIE_SERVICE_SUPPLIER_3ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]	| ["1arg-arg1-alt"]									
			"SINGLETON_DUMMIE_SERVICE_0ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| []							| ["3arg-arg1-alt",4,"3arg-arg2-alt"]
			"SINGLETON_DUMMIE_SERVICE_1ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| ["1arg-arg1"]					| []												
			"SINGLETON_DUMMIE_SERVICE_3ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]	| ["1arg-arg1-alt"]									
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| []							| ["3arg-arg1-alt",4,"3arg-arg2-alt"]
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| ["1arg-arg1"]					| []												
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]	| ["1arg-arg1-alt"]									

			unNormalizedExpected=(singleton)? constructorArgs 
												: ((!singleton&&getterArgs.size()==0)? constructorArgs : getterArgs)
			expected=[(unNormalizedExpected.size()>0)? unNormalizedExpected[0] : "",(unNormalizedExpected.size()>1)? unNormalizedExpected[1] : 0,(unNormalizedExpected.size()>2)? unNormalizedExpected[2] : ""]			
	}

	@Unroll
	def "Replace #name ServiceSupplier instance of #implementation(#args) as singleton[#singleton] with another instance that doesnt implement #adapter should raise an UnableToSetInstanceException"(){
		println(">>>>> DefaultServiceSupplierSpec >>>> Replace $name instance of $implementation($args) as singleton[$singleton] with another instance that doesnt implement $adapter should raise an UnableToSetInstanceException")

		when:
			def serviceSupplier=DefaultServiceSupplier.builder(adapter)
															.name(name)
															.singleton(singleton)
															.implementation(implementation)
															.args((Object[])args)
														.build()
			serviceSupplier.setInstance(new Integer(1))

		then:
			def e=thrown(UnableToSetInstanceException)
			
		where:
			name										| adapter				| singleton	| implementation			| args
			"DUMMIE_SERVICE_0ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| []
			"DUMMIE_SERVICE_1ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| ["1arg-arg1"]
			"DUMMIE_SERVICE_3ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
			"DUMMIE_SERVICE_SUPPLIER_0ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| []
			"DUMMIE_SERVICE_SUPPLIER_1ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| ["1arg-arg1"]
			"DUMMIE_SERVICE_SUPPLIER_3ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
			"SINGLETON_DUMMIE_SERVICE_0ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| []
			"SINGLETON_DUMMIE_SERVICE_1ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| ["1arg-arg1"]
			"SINGLETON_DUMMIE_SERVICE_3ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| []
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| ["1arg-arg1"]
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
	}
	

	@Unroll
	def "Replace ServiceSupplier #name of #implementation(#args) for #adapter as singleton[#singleton] with another supplier should keep the new one"(){
		println(">>>>> DefaultServiceSupplierSpec >>>> Replace supplier $name of $implementation for $adapter when singleton[$singleton] using $args with another supplier should keep the new one")

		when:
			def serviceSupplier=DefaultServiceSupplier.builder(adapter)
															.name(name)
															.singleton(singleton)
															.implementation(implementation)
															.args((Object[])args)
														.build()
			def dummieServiceImpl=new DummieServiceImpl();
			serviceSupplier.setSupplier({ -> dummieServiceImpl})

		then:
			serviceSupplier.getSupplier().get()==dummieServiceImpl;
			
		where:
			name										| adapter				| singleton	| implementation			| args
			"DUMMIE_SERVICE_0ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| []
			"DUMMIE_SERVICE_1ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| ["1arg-arg1"]
			"DUMMIE_SERVICE_3ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
			"DUMMIE_SERVICE_SUPPLIER_0ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| []
			"DUMMIE_SERVICE_SUPPLIER_1ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| ["1arg-arg1"]
			"DUMMIE_SERVICE_SUPPLIER_3ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
			"SINGLETON_DUMMIE_SERVICE_0ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| []
			"SINGLETON_DUMMIE_SERVICE_1ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| ["1arg-arg1"]
			"SINGLETON_DUMMIE_SERVICE_3ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| []
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| ["1arg-arg1"]
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
	}
}


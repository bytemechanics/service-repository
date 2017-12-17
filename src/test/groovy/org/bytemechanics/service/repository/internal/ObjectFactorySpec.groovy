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

package org.bytemechanics.service.repository.internal

import org.bytemechanics.service.repository.internal.ObjectFactory;
import org.bytemechanics.service.repository.mocks.DummieServiceImpl;
import java.util.logging.*
import spock.lang.Specification;
import spock.lang.Unroll

/**
 *
 * @author afarre
 */
class ObjectFactorySpec extends Specification{
	
	
	def setup(){
		Handler ch = new ConsoleHandler();
		ch.setLevel(Level.ALL)
		Logger.getLogger("org.bytemechanics.service.repository.internal.ObjectFactory").addHandler(ch)
		Logger.getLogger("org.bytemechanics.service.repository.internal.ObjectFactory").setLevel(Level.ALL)		
	}
	
	@Unroll
	def "Calling of(#objectiveClass) should return an instance of ObjectFactory with instantiation objective #objectiveClass"(){
		when:
			def objectFactory=ObjectFactory.of(objectiveClass)

		then:
			objectFactory!=null
			objectFactory instanceof ObjectFactory
			objectFactory.getToInstantiate()!=null
			objectFactory.getToInstantiate()==objectiveClass
			
		where:
			objectiveClass << [DummieServiceImpl.class]
	}
	
	@Unroll
	def "Calling with(#arguments) over an existent ObjectFactory of #objectiveClass should return an instance of ObjectFactory with instantiation objective #objectiveClass with #arguments constructor "(){
		when:
			def objectFactory=ObjectFactory.of(objectiveClass)
										.with((Object[])arguments)

		then:
			objectFactory!=null
			objectFactory instanceof ObjectFactory
			objectFactory.getToInstantiate()!=null
			objectFactory.getToInstantiate()==objectiveClass
			objectFactory.getAttributes()!=null
			objectFactory.getAttributes()==arguments
			
		where:
			objectiveClass			| arguments
			DummieServiceImpl.class	| []
			DummieServiceImpl.class	| ["1arg-arg1"]
			DummieServiceImpl.class	| ["1arg-arg1",3,"3arg-arg2"]
	}
	
	@Unroll
	def "Search constructor over ObjectFactory instance of #objectiveClass with #arguments should return an optional of #constructor"(){
		when:
			def optionalConstructor=ObjectFactory.of(objectiveClass)
										.with((Object[])arguments)
										.findConstructor()

		then:
			optionalConstructor!=null
			optionalConstructor instanceof Optional
			optionalConstructor.isPresent()
			constructor==optionalConstructor.get()
			
		where:
			objectiveClass			| arguments						| constructor
			DummieServiceImpl.class	| []							| DummieServiceImpl.class.getConstructor()
			DummieServiceImpl.class	| ["1arg-arg1"]					| DummieServiceImpl.class.getConstructor((Class[])[String.class])
			DummieServiceImpl.class	| ["1arg-arg1",3,"3arg-arg2"]	| DummieServiceImpl.class.getConstructor((Class[])[String.class,int.class,String.class])
	}

	@Unroll
	def "when object factory builds a supplier of #supplierClass with #arguments should return a supplier of #supplierClass"(){
		when:
			def supplier=ObjectFactory.of(supplierClass)
										.with((Object[])arguments)
										.supplier()

		then:
			supplier!=null
			supplierClass.equals(supplier.get().get().getClass())
			if(arguments.size()>0){
				arguments[0]==supplier.get().get().getArg1()
				if(arguments.size()>1){
					arguments[1]==supplier.get().get().getArg2()
					if(arguments.size()>2){
						arguments[2]==supplier.get().get().getArg3()
					}
				}
			}
			
		where:
			supplierClass			| arguments
			DummieServiceImpl.class	| []
			DummieServiceImpl.class	| ["1arg-arg1"]
			DummieServiceImpl.class	| ["1arg-arg1",3,"3arg-arg2"]
	}

	@Unroll
	def "when object factory builds a supplier of #supplierClass with #arguments should return a empty optional and write a log "(){
		when:
			def result=ObjectFactory.of(supplierClass)
										.with((Object[])arguments)
										.supplier()
										.get()

		then:
			result!=null
			!result.isPresent()
			
		where:
			supplierClass			| arguments
			DummieServiceImpl.class	| ["1arg-arg1",3,false,"3arg-arg2"]
	}
}


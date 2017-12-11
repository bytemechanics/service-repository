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

import org.bytemechanics.service.repository.ServiceSupplier;
import org.bytemechanics.service.repository.mocks.DummieService;
import org.bytemechanics.service.repository.mocks.DummieServiceImpl;
import spock.lang.Specification;
import spock.lang.Unroll

/**
 * @author afarre
 */
class ServiceSupplierSpec extends Specification{
	
	@Unroll
	def "when generate supplier of #adapter from #implementation with #arguments should return a supplier that returns an instance of #implementation"(){
	
		when:
			def supplier=ServiceSupplier.generateSupplier("mySupplier",implementation,(Object[])arguments)

		then:
			supplier!=null
			service.isAssignableFrom(supplier.get().getClass())
			implementation.equals(supplier.get().getClass())
			if(arguments.size()>0){
				arguments[0]==supplier.get().getArg1()
				if(arguments.size()>1){
					arguments[1]==supplier.get().getArg2()
					if(arguments.size()>2){
						arguments[2]==supplier.get().getArg3()
					}
				}
			}
			
		where:
			service					| implementation			| arguments
			DummieService.class		| DummieServiceImpl.class	| []
			DummieService.class		| DummieServiceImpl.class	| ["1arg-arg1"]
			DummieService.class		| DummieServiceImpl.class	| ["1arg-arg1",3,"3arg-arg2"]
	}
}


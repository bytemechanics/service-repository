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
import org.bytemechanics.service.repository.exceptions.UnableToSetInstanceException;
import org.bytemechanics.service.repository.mocks.DummieService;
import org.bytemechanics.service.repository.mocks.DummieServiceImpl;
import spock.lang.Specification;
import spock.lang.Unroll

/**
 * @author afarre
 */
class DefaultServiceSupplierSpec extends Specification{
	
	@Unroll
	def "Call constructor with #name using #adapter and #implementation marked as #singleton with #args should create a correct instance"(){
		when:
			def serviceSupplier=new DefaultServiceSupplier(name,adapter,singleton,implementation,(Object[])args)

		then:
			serviceSupplier!=null
			serviceSupplier.getInstance()==null
			serviceSupplier.getName()==name
			serviceSupplier.getAdapter()==adapter
			serviceSupplier.getSupplier()!=null
			serviceSupplier.getSupplier().get() instanceof DummieServiceImpl
			
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
	def "Replace instance with another instance that doesnt implement #adapter should raise an exception"(){
		when:
			def serviceSupplier=new DefaultServiceSupplier(name,adapter,singleton,implementation,(Object[])args)
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
}


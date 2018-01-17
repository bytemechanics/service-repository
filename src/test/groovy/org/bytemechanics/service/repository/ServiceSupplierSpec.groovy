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
import java.util.logging.*


/**
 * @author afarre
 */
class ServiceSupplierSpec extends Specification{

	def setupSpec(){
		println(">>>>> ServiceSupplierSpec >>>> setupSpec")
		final InputStream inputStream = ServiceSupplierSpec.class.getResourceAsStream("/logging.properties");
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
	def "When generates supplier of #adapter from #implementation with #arguments should return a supplier that returns an instance of #implementation"(){
		println(">>>>> ServiceSupplierSpec >>>> when generate supplier of $adapter from $implementation with $arguments should return a supplier that returns an instance of $implementation")
	
		when:
			def supplier=ServiceSupplier.generateSupplier("mySupplier",implementation,(Object[])arguments)
			def instance=(supplier!=null)? supplier.get() : null
			def args=(instance!=null)? [instance.getArg1(),instance.getArg2(),instance.getArg3()] : null

		then:
			supplier!=null
			instance!=null
			implementation.equals(instance.getClass())
			expected==args
			
		where:
			adapter					| implementation			| arguments
			DummieService.class		| DummieServiceImpl.class	| []
			DummieService.class		| DummieServiceImpl.class	| ["1arg-arg1"]
			DummieService.class		| DummieServiceImpl.class	| ["1arg-arg1",3,"3arg-arg2"]
			expected=[(arguments.size()>0)? arguments[0] : "",(arguments.size()>1)? arguments[1] : 0,(arguments.size()>2)? arguments[2] : ""]			
	}
}


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

	
	@Unroll
	def "Call ServiceSupplier.from(#name,#adapter,#singleton,#implementation,#args) should create the #expected instance"(){
		println(">>>>> ServiceSupplierSpec >>>> Call DefaultServiceSupplier($name,$adapter,$singleton,$implementation,$args) should create the $expected  instance")

		when:
			def serviceSupplier=ServiceSupplier.from(name,adapter,singleton,implementation,(Object[])args)

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
	def "Call ServiceSupplier.from(#name,#adapter,#implementation,#args) should create the #expected instance"(){
		println(">>>>> ServiceSupplierSpec >>>> Call DefaultServiceSupplier($name,$adapter,$implementation,$args) should create the $expected  instance")

		when:
			def serviceSupplier=ServiceSupplier.from(name,adapter,implementation,(Object[])args)

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
			name										| adapter				| implementation			| args
			"DUMMIE_SERVICE_0ARG"						| DummieService.class	| DummieServiceImpl.class	| []
			"DUMMIE_SERVICE_1ARG"						| DummieService.class	| DummieServiceImpl.class	| ["1arg-arg1"]
			"DUMMIE_SERVICE_3ARG"						| DummieService.class	| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
			"DUMMIE_SERVICE_SUPPLIER_0ARG"				| DummieService.class	| DummieServiceImpl.class	| []
			"DUMMIE_SERVICE_SUPPLIER_1ARG"				| DummieService.class	| DummieServiceImpl.class	| ["1arg-arg1"]
			"DUMMIE_SERVICE_SUPPLIER_3ARG"				| DummieService.class	| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]
	
			expected=[(args.size()>0)? args[0] : "",(args.size()>1)? args[1] : 0,(args.size()>2)? args[2] : ""]			
	}
	
	@Unroll
	def "Call ServiceSupplier.from(#name,#adapter,#singleton,#implementation,#supplier) should create the #expected instance"(){
		println(">>>>> ServiceSupplierSpec >>>> Call DefaultServiceSupplier($name,$adapter,$singleton,$implementation,$supplier) should create the $expected  instance")

		when:
			def serviceSupplier=ServiceSupplier.from(name,adapter,implementation,singleton,supplier)

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
			name										| adapter				| singleton	| implementation			| args							| supplier
			"DUMMIE_SERVICE_0ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| []							| { -> new DummieServiceImpl()}
			"DUMMIE_SERVICE_1ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| ["1arg-arg1"]					| { -> new DummieServiceImpl("1arg-arg1")}
			"DUMMIE_SERVICE_3ARG"						| DummieService.class	| false		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]	| { -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")}
			"DUMMIE_SERVICE_SUPPLIER_0ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| []							| { -> new DummieServiceImpl()}
			"DUMMIE_SERVICE_SUPPLIER_1ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| ["1arg-arg1"]					| { -> new DummieServiceImpl("1arg-arg1")}
			"DUMMIE_SERVICE_SUPPLIER_3ARG"				| DummieService.class	| false		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]	| { -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")}
			"SINGLETON_DUMMIE_SERVICE_0ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| []							| { -> new DummieServiceImpl()}
			"SINGLETON_DUMMIE_SERVICE_1ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| ["1arg-arg1"]					| { -> new DummieServiceImpl("1arg-arg1")}
			"SINGLETON_DUMMIE_SERVICE_3ARG"				| DummieService.class	| true		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]	| { -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")}
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| []							| { -> new DummieServiceImpl()}
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| ["1arg-arg1"]					| { -> new DummieServiceImpl("1arg-arg1")}
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG"	| DummieService.class	| true		| DummieServiceImpl.class	| ["3arg-arg1",3,"3arg-arg2"]	| { -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")}

			expected=[(args.size()>0)? args[0] : "",(args.size()>1)? args[1] : 0,(args.size()>2)? args[2] : ""]			
	}
	
	@Unroll
	def "Call ServiceSupplier.from(#name,#adapter,#singleton,#supplier) should create the #expected instance"(){
		println(">>>>> ServiceSupplierSpec >>>> Call DefaultServiceSupplier($name,$adapter,$singleton,$supplier) should create the $expected  instance")

		when:
			def serviceSupplier=ServiceSupplier.from(name,adapter,singleton,supplier)

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
			name										| adapter				| singleton		| args							| supplier
			"DUMMIE_SERVICE_0ARG"						| DummieService.class	| false			| []							| { -> new DummieServiceImpl()}
			"DUMMIE_SERVICE_1ARG"						| DummieService.class	| false			| ["1arg-arg1"]					| { -> new DummieServiceImpl("1arg-arg1")}
			"DUMMIE_SERVICE_3ARG"						| DummieService.class	| false			| ["3arg-arg1",3,"3arg-arg2"]	| { -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")}
			"DUMMIE_SERVICE_SUPPLIER_0ARG"				| DummieService.class	| false			| []							| { -> new DummieServiceImpl()}
			"DUMMIE_SERVICE_SUPPLIER_1ARG"				| DummieService.class	| false			| ["1arg-arg1"]					| { -> new DummieServiceImpl("1arg-arg1")}
			"DUMMIE_SERVICE_SUPPLIER_3ARG"				| DummieService.class	| false			| ["3arg-arg1",3,"3arg-arg2"]	| { -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")}
			"SINGLETON_DUMMIE_SERVICE_0ARG"				| DummieService.class	| true			| []							| { -> new DummieServiceImpl()}
			"SINGLETON_DUMMIE_SERVICE_1ARG"				| DummieService.class	| true			| ["1arg-arg1"]					| { -> new DummieServiceImpl("1arg-arg1")}
			"SINGLETON_DUMMIE_SERVICE_3ARG"				| DummieService.class	| true			| ["3arg-arg1",3,"3arg-arg2"]	| { -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")}
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_0ARG"	| DummieService.class	| true			| []							| { -> new DummieServiceImpl()}
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_1ARG"	| DummieService.class	| true			| ["1arg-arg1"]					| { -> new DummieServiceImpl("1arg-arg1")}
			"SINGLETON_DUMMIE_SERVICE_SUPPLIER_3ARG"	| DummieService.class	| true			| ["3arg-arg1",3,"3arg-arg2"]	| { -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")}

			expected=[(args.size()>0)? args[0] : "",(args.size()>1)? args[1] : 0,(args.size()>2)? args[2] : ""]			
	}
	
	@Unroll
	def "Call ServiceSupplier.from(#name,#adapter,#supplier) should create the #expected instance"(){
		println(">>>>> ServiceSupplierSpec >>>> Call DefaultServiceSupplier($name,$adapter,$supplier) should create the $expected  instance")

		when:
			def serviceSupplier=ServiceSupplier.from(name,adapter,supplier)

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
			name										| adapter				| args							| supplier
			"DUMMIE_SERVICE_0ARG"						| DummieService.class	| []							| { -> new DummieServiceImpl()}
			"DUMMIE_SERVICE_1ARG"						| DummieService.class	| ["1arg-arg1"]					| { -> new DummieServiceImpl("1arg-arg1")}
			"DUMMIE_SERVICE_3ARG"						| DummieService.class	| ["3arg-arg1",3,"3arg-arg2"]	| { -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")}
			"DUMMIE_SERVICE_SUPPLIER_0ARG"				| DummieService.class	| []							| { -> new DummieServiceImpl()}
			"DUMMIE_SERVICE_SUPPLIER_1ARG"				| DummieService.class	| ["1arg-arg1"]					| { -> new DummieServiceImpl("1arg-arg1")}
			"DUMMIE_SERVICE_SUPPLIER_3ARG"				| DummieService.class	| ["3arg-arg1",3,"3arg-arg2"]	| { -> new DummieServiceImpl("3arg-arg1",3,"3arg-arg2")}

			expected=[(args.size()>0)? args[0] : "",(args.size()>1)? args[1] : 0,(args.size()>2)? args[2] : ""]			
	}

	def "When service supplier dispose is call over a singleton, with supplier provided, the next instance should be distinct"(){
		println(">>>>> ServiceSupplierSpec >>>> When service supplier dispose is call over a singleton, with supplier provided,  the next instance should be distinct")

		setup:
			def serviceSupplier=ServiceSupplier.from("DUMMIE_SERVICE_0ARG",DummieService.class,true,{ -> new DummieServiceImpl()})
			def original=serviceSupplier.get()
			
		when:
			serviceSupplier.dispose()

		then:
			serviceSupplier.get()!=original
	}
	def "When service supplier dispose is call over a singleton, with supplier provided replaced, the next instance should be distinct"(){
		println(">>>>> ServiceSupplierSpec >>>> When service supplier dispose is call over a singleton, with supplier provided replaced,  the next instance should be distinct")

		setup:
			def serviceSupplier=ServiceSupplier.from("DUMMIE_SERVICE_0ARG",DummieService.class,true,{ -> new DummieServiceImpl()})
			def dummieService=new DummieServiceImpl();
			serviceSupplier.setSupplier({ -> dummieService})
			def original=serviceSupplier.get()
			
		when:
			serviceSupplier.dispose()

		then:
			serviceSupplier.get()!=original
	}
	def "When service supplier dispose is call over a singleton, with implementation, the next instance should be distinct"(){
		println(">>>>> ServiceSupplierSpec >>>> When service supplier dispose is call over a singleton, with implementation,  the next instance should be distinct")

		setup:
			def serviceSupplier=ServiceSupplier.from("DUMMIE_SERVICE_0ARG",DummieService.class,true,DummieServiceImpl.class)
			def original=serviceSupplier.get()
			
		when:
			serviceSupplier.dispose()

		then:
			serviceSupplier.get()!=original
	}
	def "When service supplier dispose is call over a singleton, with implementation and supplier replaced, the next instance should be distinct"(){
		println(">>>>> ServiceSupplierSpec >>>> When service supplier dispose is call over a singleton, with implementation and supplier replaced,  the next instance should be distinct")

		setup:
			def serviceSupplier=ServiceSupplier.from("DUMMIE_SERVICE_0ARG",DummieService.class,true,DummieServiceImpl.class)
			def dummieService=new DummieServiceImpl();
			serviceSupplier.setSupplier({ -> dummieService})
			def original=serviceSupplier.get()
			
			
		when:
			serviceSupplier.dispose()

		then:
			serviceSupplier.get()!=original
	}
}


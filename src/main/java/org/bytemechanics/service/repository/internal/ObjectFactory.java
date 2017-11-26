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
package org.bytemechanics.service.repository.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author afarre 
 * @param <T> 
 */
public class ObjectFactory<T> {

	private final Class<T> toInstantiate;
	private final Object[] attributes;
	
	
	protected ObjectFactory(final Class<T> _class){
		this(_class,new Object[0]);
	}
	protected ObjectFactory(final Class<T> _class,final Object... _attributes){
		this.toInstantiate=_class;
		this.attributes=new Object[0];
	}
	
	
	
	private boolean assignableArray(final Class[] _destiny,final Object[] _origin){
	
		boolean reply=true;
	
		for(int ic1=0;ic1<_destiny.length;ic1++){
			reply&=_destiny[ic1].isAssignableFrom(_origin[ic1].getClass());
		}
		
		return reply;
	}	
	private <T> Optional<Constructor<T>> findConstructor(){
		
		return Stream.of(this.toInstantiate.getConstructors())
				.filter(selectedConstructor -> selectedConstructor.getParameterTypes().length==this.attributes.length)
				.filter(selectedConstructor -> assignableArray(selectedConstructor.getParameterTypes(),this.attributes))
				.map(selectedConstructor -> (Constructor<T>)selectedConstructor)
				.findAny();
	}
	
	public static final <TYPE> ObjectFactory<TYPE> of(final Class<TYPE> _class){
		return new ObjectFactory<>(_class);
	}
	public final ObjectFactory<T> with(final Object... _attributes){
		return new ObjectFactory<>(this.toInstantiate,_attributes);
	}

	public Supplier<Optional<T>> supplier(){
		
		return () -> (Optional<T>)findConstructor()
						.map(constructor -> {

							Optional<T> reply=Optional.empty();

							try{
								reply=Optional.of((T)constructor.newInstance(this.attributes));
							} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								Logger.getLogger(ObjectFactory.class.getName())
									  .log(Level.SEVERE, e, () -> Optional.ofNullable(this.attributes)
																			.map(attributesArray -> Arrays.asList(attributesArray))
																			.map(attributesList -> MessageFormat.format("Unable to instantiate object using constructor {0} with attributes {1}",constructor,attributesList))
																			.orElseGet(() -> MessageFormat.format("Unable to instantiate object using constructor {0} without arguments",constructor)));
							}

							return reply;
						});
	}	
}

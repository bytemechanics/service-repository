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
 * Factory to create objects
 * @author afarre 
 * @param <T> Type of class to instantiate
 */
public class ObjectFactory<T> {

	private final Class<T> toInstantiate;
	private final Object[] attributes;
	
	
	protected ObjectFactory(final Class<T> _class){
		this(_class,new Object[0]);
	}
	protected ObjectFactory(final Class<T> _class,final Object... _attributes){
		this.toInstantiate=_class;
		this.attributes=_attributes;
	}

	
	public Class<T> getToInstantiate() {
		return toInstantiate;
	}
	public Object[] getAttributes() {
		return attributes;
	}
	
	
	private Class autobox(final Class _class){
	
		Class reply=_class;
		
		if(_class.isPrimitive()){
			if(void.class.equals(_class)){
				reply=Void.class;
			}else if(byte.class.equals(_class)){
				reply=Byte.class;
			}else if(boolean.class.equals(_class)){
				reply=Boolean.class;
			}else if(char.class.equals(_class)){
				reply=Character.class;
			}else if(short.class.equals(_class)){
				reply=Short.class;
			}else if(int.class.equals(_class)){
				reply=Integer.class;
			}else if(long.class.equals(_class)){
				reply=Long.class;
			}else if(float.class.equals(_class)){
				reply=Float.class;
			}else if(double.class.equals(_class)){
				reply=Double.class;
			}
		}
		
		return reply;
	}

	private boolean assignableArray(final Class[] _destiny,final Object[] _origin){
	
		boolean reply=true;
	
		for(int ic1=0;ic1<_destiny.length;ic1++){
			final Class boxedDestiny=autobox(_destiny[ic1]);
			final Class boxedOrigin=autobox(_origin[ic1].getClass());
			reply&=boxedDestiny.isAssignableFrom(boxedOrigin);
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
		
		return () -> findConstructor()
						.map(constructor -> {

							T reply=null;

							try{
								reply=(T)constructor.newInstance(this.attributes);
							} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								Logger.getLogger(ObjectFactory.class.getName())
									  .log(Level.SEVERE, e, () -> Optional.ofNullable(this.attributes)
																			.map(Arrays::asList)
																			.map(attributesList -> MessageFormat.format("Unable to instantiate object using constructor {0} with attributes {1}",constructor,attributesList))
																			.orElseGet(() -> MessageFormat.format("Unable to instantiate object using constructor {0} without arguments",constructor)));
							}

							return reply;
						});
	}	
}

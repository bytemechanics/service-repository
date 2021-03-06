# Service Repository
[![Latest version](https://maven-badges.herokuapp.com/maven-central/org.bytemechanics/service-repository/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.bytemechanics/service-repository/badge.svg)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.bytemechanics%3Aservice-repository&metric=alert_status)](https://sonarcloud.io/dashboard/index/org.bytemechanics%3Aservice-repository)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.bytemechanics%3Aservice-repository&metric=coverage)](https://sonarcloud.io/dashboard/index/org.bytemechanics%3Aservice-repository)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Service Repository it's a library to simplify the task to define and unify the distinct services existing inside any application.
Allows:
* Create stateful services (singletons)
* Create stateless services acting as service factory 

## Motivation
With the dependency injection development has become easier and faster, but with large projects increase the dificulty to start working with a preexistent project and it's difficult to understand by novice developers. For this reason we propose to go back to the old style factories, but with more fashion approach
Of course you are free to use it as base for another dependency injection mechanism, but this is not the final intention

## Quick start
1. First of all include the Jar file in your compile and execution classpath.
### Maven
```Maven
	<dependency>
		<groupId>org.bytemechanics</groupId>
		<artifactId>service-repository</artifactId>
		<version>X.X.X</version>
	</dependency>
```
### Graddle
```Gradle
dependencies {
    compile 'org.bytemechanics:service-repository:X.X.X'
}
```
1. Create your service repository
```Java
package mypackage;
import org.bytemechanics.service.repository.ServiceSupplier;
import org.bytemechanics.service.repository.beans.DefaultServiceSupplier;
import org.bytemechanics.service.repository.ServiceRepository;
public enum MyServiceRepository implements ServiceRepository{
	MY_SERVICE_0ARG(MyService.class,MyServiceImpl.class),
	MY_SERVICE_SUPPLIER_0ARG(MyService.class,() -> new MyServiceImpl()),
	MY_SINGLETON_SERVICE_0ARG(MyService.class,true,MyServiceImpl.class),
	MY_SINGLETON_SERVICE_SUPPLIER_0ARG(MyService.class,true,() -> new MyServiceImpl()),
	;	
	private final ServiceSupplier serviceSupplier;	
	<T> MyServiceRepository(final Class<T> _adapter,final Class<? extends T> _implementation,final Object... _args){
		this.serviceSupplier=ServiceSupplier.builder(_adapter)
												.name(name())
												.implementation(_implementation)
												.args(_args)
											.build();
	}
	<T> MyServiceRepository(final Class<T> _adapter,final Supplier<? extends T> _implementationSupplier){
		this.serviceSupplier=ServiceSupplier.builder(_adapter)
												.name(name())
												.supplier(_implementationSupplier)
											.build();
	}
	<T> MyServiceRepository(final Class<T> _adapter,final boolean _singleton,final Class<? extends T> _implementation,final Object... _args){
		this.serviceSupplier=ServiceSupplier.builder(_adapter)
												.name(name())
												.singleton(_singleton)
												.implementation(_implementation)
												.args(_args)
											.build();
	}
	<T> MyServiceRepository(final Class<T> _adapter,final boolean _singleton,final Supplier<? extends T> _implementationSupplier){
		this.serviceSupplier=ServiceSupplier.builder(_adapter)
												.name(name())
												.singleton(_singleton)
												.supplier(_implementationSupplier)
											.build();
	}
	@Override
	public ServiceSupplier getServiceSupplier() {
		return this.serviceSupplier;
	}
	public static final void startup(){
		ServiceRepository.startup(Stream.of(MyServiceRepository.values()));
	}
	public static final void shutdown(){
		ServiceRepository.shutdown(Stream.of(MyServiceRepository.values()));
	}
	public static final void reset(){
		ServiceRepository.reset(Stream.of(MyServiceRepository.values()));
	}
}
```
1. get service instance
### Directly (with exceptions)
```Java
MyServiceRepository.MY_SINGLETON_SERVICE_0ARG.get();
```
### Directly (with exceptions) casted
```Java
MyServiceRepository.MY_SINGLETON_SERVICE_0ARG.get(MyService.class);
```
### Optional (without exceptions)
```Java
MyServiceRepository.MY_SINGLETON_SERVICE_0ARG.tryGet();
```
### Optional (without exceptions) casted
```Java
MyServiceRepository.MY_SINGLETON_SERVICE_0ARG.tryGet(MyService.class);
```

# Service Repository
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

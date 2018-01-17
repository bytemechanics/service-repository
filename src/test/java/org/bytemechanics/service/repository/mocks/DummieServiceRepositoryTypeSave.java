/*
 * Copyright 2018 Byte Mechanics.
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
package org.bytemechanics.service.repository.mocks;

/**
 *
 * @author afarre
 */
public class DummieServiceRepositoryTypeSave {
	
	public DummieService testArg0(){
		return DummieServiceRepositoryNoErrors.SINGLETON_DUMMIE_SERVICE_0ARG
									.get(DummieService.class);
	}
	public DummieService testArg1(){
		return DummieServiceRepositoryNoErrors.SINGLETON_DUMMIE_SERVICE_1ARG
									.get(DummieService.class);
	}
	public DummieService testArg3(){
		return DummieServiceRepositoryNoErrors.SINGLETON_DUMMIE_SERVICE_3ARG
									.get(DummieService.class);
	}
	
	public DummieService testArg0Try(){
		return DummieServiceRepositoryNoErrors.SINGLETON_DUMMIE_SERVICE_0ARG
									.tryGet(DummieService.class)
										.get();
	}
	public DummieService testArg1Try(){
		return DummieServiceRepositoryNoErrors.SINGLETON_DUMMIE_SERVICE_1ARG
									.tryGet(DummieService.class)
										.get();
	}
	public DummieService testArg3Try(){
		return DummieServiceRepositoryNoErrors.SINGLETON_DUMMIE_SERVICE_3ARG
									.tryGet(DummieService.class)
										.get();
	}
}

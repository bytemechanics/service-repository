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
package org.bytemechanics.service.repository.mocks;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 * @author afarre
 */
public class DummieServiceImpl implements DummieService,Closeable {

	private boolean closed=false;
	private String arg1;
	private int arg2;
	private String arg3;
	private boolean arg4;
	
	
	public DummieServiceImpl(){
		this("no0arg",0,"no0arg",false);
	}
	public DummieServiceImpl(String _arg1){
		this(_arg1,0,"no1arg",false);
	}
	public DummieServiceImpl(String _arg1,int _arg2,String _arg3){
		this(_arg1,_arg2,_arg3,false);
	}
	private DummieServiceImpl(String _arg1,int _arg2,String _arg3,boolean _arg4){
		this.arg1=_arg1;
		this.arg2=_arg2;
		this.arg3=_arg3;
		this.arg4=_arg4;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}
	@Override
	public String getArg1() {
		return arg1;
	}
	@Override
	public int getArg2() {
		return arg2;
	}
	@Override
	public String getArg3() {
		return arg3;
	}
	@Override
	public boolean isArg4() {
		return arg4;
	}
	
	
	@Override
	public void close() throws IOException {
		this.closed=true;
	}
}

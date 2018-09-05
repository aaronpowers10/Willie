/*
 *
 *  Copyright (C) 2017 Aaron Powers
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package willie.output;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ReportRow {
	
	private ArrayList<DataEntry> entries;
	
	public ReportRow(){
		entries = new ArrayList<DataEntry>();
	}
	
	public void putReal(double value){
		entries.add(new RealEntry(value));
	}
	
	public void putInteger(int value){
		entries.add(new IntegerEntry(value));
	}
	
	public void putAlpha(String value){
		entries.add(new AlphaEntry(value));
	}
	
	public void reset(){
		entries = new ArrayList<DataEntry>();
	}
	
	public void write(FileWriter output) throws IOException{
		for(DataEntry entry: entries){
			output.write(entry.output() + ",");
		}
		output.write(System.lineSeparator());
	}

}

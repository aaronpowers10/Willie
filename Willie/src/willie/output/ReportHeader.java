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

public class ReportHeader {
	
	private ArrayList<String> titles;
	private ArrayList<Integer> numDataPoints;
	private ArrayList<String> dataHeaders;
	private ArrayList<String> dataUnits;
	
	public ReportHeader(){
		titles = new ArrayList<String>();
		numDataPoints = new ArrayList<Integer>();
		dataHeaders = new ArrayList<String>();
		dataUnits = new ArrayList<String>();
	}
	
	public void addTitle(String title,int numDataPoints){
		titles.add(title);
		this.numDataPoints.add(numDataPoints);		
	}
	
	public void addDataHeader(String dataHeader, String units){
		dataHeaders.add(dataHeader);
		dataUnits.add(units);
	}
	
	public void write(FileWriter output) throws IOException{
		for(int i=0;i<titles.size();i++){
			output.write(titles.get(i));
			for(int j=0;j<numDataPoints.get(i);j++){
				output.write(",");
			}
		}
		output.write(System.lineSeparator());
		
		for(String variableHeader: dataHeaders){
			output.write(variableHeader + ",");
		}
		output.write(System.lineSeparator());
		
		for(String units: dataUnits){
			output.write(units + ",");
		}
		
		output.write(System.lineSeparator());
	}
	
	public int numVariables(){
		return dataHeaders.size();
	}
	
	public int numObjects(){
		return titles.size();
	}

}

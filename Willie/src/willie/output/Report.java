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

public class Report {
	
	private ReportHeader header;
	private ReportRow row;
	private FileWriter output;
	
	public Report(String fileName){
		try {
			output = new FileWriter(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}	
		header = new ReportHeader();
		row = new ReportRow();
	}
	
	public void addTitle(String title,int numDataPoints){
		header.addTitle(title,numDataPoints);
	}
	
	public void addDataHeader(String dataHeader, String units){
		header.addDataHeader(dataHeader, units);
	}
	
	public void putReal(double value){
		row.putReal(value);
	}
	
	public void putInteger(int value){
		row.putInteger(value);
	}
	
	public void putAlpha(String value){
		row.putAlpha(value);
	}
	
	public void writeHeader() throws IOException{
		header.write(output);
	}
	
	public void writeRow() throws IOException{
		row.write(output);
		row.reset();
	}
	
	
	
	public void close() throws IOException{
		output.close();
		output = null;
	}

}

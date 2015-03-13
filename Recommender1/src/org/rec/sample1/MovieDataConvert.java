package org.rec.sample1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MovieDataConvert {

	/**
	 * cat u.data | cut -f1,2,3 | tr "\\t" ","
	 * @throws IOException 
	 * 
	 */
	public void formatDataset(String inputFile, String outputFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		
		String line;
		while((line = br.readLine()) != null) {
			String[] values = line.split("::", -1);
			bw.write(values[0] + "," + values[1] + "," + values[2] + "\n");
		}
		
		br.close();
		bw.close();

	}
	public static void main(String[] args) throws IOException {
		MovieDataConvert mc = new MovieDataConvert();
		mc.formatDataset(new String("Data/ratings.dat"), new String("Data/movies_1m.csv"));
		
	}

}
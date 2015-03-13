/**
 * 
 */
package org.rec.sample1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ItemBasedGenreType extends ItemCollaborative{

	public void getGenre(long itemid, String genreFile) throws FileNotFoundException{
		Scanner sc = new Scanner(new File(genreFile));
		
	}
}

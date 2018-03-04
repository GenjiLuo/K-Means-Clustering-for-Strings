package main.app;

import java.util.Scanner;

import com.k.means.clustering.algorithm.KMeans;

public class MainApp {
	
	private static final String INPUT_FILE = "./inputdata/filelist.txt";

	public static void main(String[] args) {

		
		KMeans kMeans = new KMeans(INPUT_FILE);
		
		Scanner reader = new Scanner (System.in);
		boolean isValid = false;
		int numOfClusters = 0;

		do {
			System.out.println("Pleas enter number of clusters: ");
			if (reader.hasNextInt()) {
				numOfClusters = reader.nextInt();
				//number of clusters can't exceed file's rows
				if (numOfClusters > 0 && numOfClusters <= kMeans.getNumOfRows()) {
					isValid = true;
					reader.close();
				}
			} else {
				System.out.println("the number that you enter is not valis, please re-enter");
				reader.next();
			}

		} while (!isValid);
		
		System.out.println("Running k-means clustering for K =" + numOfClusters);
		kMeans.runKMeans(numOfClusters);
		
		
	}

}

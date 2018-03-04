package com.k.means.clustering.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.List;

import com.jaro.winkler.algorithm.JaroWinkler;


public class KMeans {

	private static final double DEFAULT_THRESHOLD = 0.7;
	private static final int MAX_ITERATIONS = 10;
	private static final int NUM_OF_DIMENSIONS = 2;
	
    
	private int numOfRows;
	private int itrCounter;
	private int noOfClusters;
	private String fileName;
	private JaroWinkler jw;

	private int cluster[];
	private String centroid[][];
	private String data[];

	/** Constructor - Instantiate the Jaro Winkler helper class 
	 *                and reads the data from the input file.  
	 * 
	**/ 
	public KMeans(String fileName) {
		
        this.fileName = fileName;
		jw = new JaroWinkler();
		readDataFromFile();

	}
    
	/** 
	 * 
	 *   Runner method to trigger the k-means clustering flow.
	 *   
	 **/
	public void runKMeans(int noOfClusters) {

		this.noOfClusters = noOfClusters;
		populateSeeds();
		runKMeans(data, noOfClusters, centroid);

	}

	/**
	 * Compute k-means clustering with the specified number of clusters. 
	 * Since the inputs are strings, the distance calculation, method performs the ranking (distance)
	 * using Jaro-Winkler similarity algorithm.
	 * 
	 * @param data
	 *            - contains the input file data.
	 * @param noofclusters
	 *            - number of clusters
	 * @param centroid
	 *            - array with the population of the seeds.
	 * 
	 */

	private String[][] runKMeans(String data[], int noOfClusters, String centroid[][]) {

		double distance[][] = new double[noOfClusters][data.length];
		cluster = new int[data.length];
		double clusternodecount[] = new double[noOfClusters];

		centroid[0] = centroid[1];
		System.out.println("========== Starting to calculate distances =========");
		
        //Calculate the distance by passing the seeds and the data array items.
		for (int i = 0; i < noOfClusters; i++) {
			for (int j = 0; j < data.length; j++) {
				distance[i][j] = jw.similarity(data[j], centroid[0][i]);
				System.out.print(distance[i][j] + " ,");
			}
			System.out.println();
		}
        //Sort the population based on the rank/distance (Jaro Winker percent match)
		for (int i = 0; i < distance[0].length; i++) {
			int row = 0;
			double max = 0.0;
			int j;
			for (j = 0; j < distance.length; j++) {
				if (distance[j][i] > max) {
					max = distance[j][i];
					row = j;

				}
			}
			//Maintained for the numeric algorithm (end condition: cluster's mean previous run equals to current)
			centroid[1][row] = centroid[1][row] + data[j];
			clusternodecount[row] = clusternodecount[row] + 1;
			
			cluster[i] = row;

		}

		System.out.println("======================================== ");

		System.out.println("New Clusters are ");
		for (int i = 0; i < noOfClusters; i++) {
			System.out.print("C" + (i + 1) + ": ");
			for (int l = 0; l < data.length; l++) {
				if (cluster[l] == i)
					System.out.print(data[l] + " ,");

			}
			System.out.println();
		}
		System.out.println("======================================== ");
		
        //Check for cluster's match percentages.
		double disSum = 0.0;
		boolean isAchived = true;
		for (int i = 0; i < noOfClusters; i++) {
			for (int j = 0; j < numOfRows; j++) {
				disSum += distance[i][j];
			}

		}
		
		System.out.println("Sum of the distances:" + disSum);
		System.out.println("====Avg Distance from Seeds: " + (disSum / (numOfRows * noOfClusters)));
		
		//Method's end condition check.
		if ((disSum / (numOfRows * noOfClusters)) <= DEFAULT_THRESHOLD && itrCounter < MAX_ITERATIONS) {
			isAchived = false;
			populateSeeds();
		}
       
		if (!isAchived) {
			itrCounter++;
			runKMeans(data, noOfClusters, centroid);
		}

		else {

			writeClustersToFile();
		}

		return centroid;

	}

	/**
	 * 
	 * Iterates over the clusters and writes the data to output files.
	 *
	 */
	private void writeClustersToFile() {
		System.out.println("==========Write clusters to file=========");
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh mm ss");
		String time = dateFormat.format(now);
		
        //Create new folder and write the clusters data to files
		try {
			File dir = new File("./outputdata" + time + "/");
			dir.mkdir();

			for (int i = 0; i < noOfClusters; i++) {
				File file = new File(dir, "Cluster_" + (i + 1) + ".txt");
				FileWriter fileWriter = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fileWriter);
				out.write("Cluster " + (i + 1) + " :");
				out.newLine();
				for (int j = 0; j < data.length; j++) {
					if (cluster[j] == i) {
						out.write(data[j] + " ,");
						out.newLine();
					}

				}
				out.close();
			}
		} catch (IOException exc) {
			exc.printStackTrace();
		}

	}

	/**
	 * 
	 * Populates the seeds in the 'centroid' array
	 * 
	 */
	private void populateSeeds() {

		System.out.println("==========Random numbers for new centroid=========");

		Random rand = new Random();
		centroid = new String[NUM_OF_DIMENSIONS][noOfClusters];
		List<Integer> randlist = new ArrayList<>();
        int index = 0;

        //avoid duplicates.
		for (int i = 0; i < noOfClusters; i++) {
			do {
				index = rand.nextInt(numOfRows);
				System.out.println("Random cell: " + index);
			} while (randlist.contains(index));
			System.out.println("adding: " + index);
			randlist.add(index);

		}

		for (int i = 0; i < noOfClusters; i++) {
			centroid[1][i] = data[randlist.get(i)];
		}

	}

	/**
	 * 
	 * Reads the input file and populates the data set for further processing.
	 * 
	 */
	private void readDataFromFile() {
		System.out.println("==========Start read data from file=========");
		String readLine;
		ArrayList<String> listdata = new ArrayList<>();
		try {
			//"./inputdata/filelist.txt"
			File file = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(file));

			while ((readLine = br.readLine()) != null) {
				listdata.add(readLine);
				numOfRows++;
			}
			System.out.println("Number of rows in file: " + numOfRows);
			br.close();
		} catch (IOException exc) {
            
			exc.printStackTrace();
		}
		System.out.println("==========Populate data set=========");
		data = new String[listdata.size()];
		data = listdata.toArray(data);
	}

	/**
	 * Returns the current value of the rows from the input file.
	 *
	 * @return the current value of the numOfRows
	 */
	public int getNumOfRows() {
		return numOfRows;
	}

}

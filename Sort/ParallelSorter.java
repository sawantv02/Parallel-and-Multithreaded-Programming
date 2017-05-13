/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HW1part2;

/**
 *
 * @author vishakha
 */


//import java.util.LinkedList;
import HW1part2.*;
import java.util.*;

/**
 * This class runs <code>numThreads</code> instances of
 * <code>ParallelSorterWorker</code> in parallel to find the maximum
 * <code>Integer</code> in a <code>LinkedList</code>.
 */
public class ParallelSorter {
	
	int numThreads;
	static ArrayList<ParallelSorterWorker> workers; // = new ArrayList<ParallelMaximizerWorker>(numThreads);

	public ParallelSorter(int numThreads) {
		workers = new ArrayList<ParallelSorterWorker>(numThreads);
                this.numThreads=numThreads;
//                 for(int i=0;i<numThreads;i++)
//                    workers.add(null);
                
	}


	
	public static void main(String[] args) {
		int numThreads = 4; // number of threads for the maximizer
		int numElements = 10; // number of integers in the list
		
		ParallelSorter sorter = new ParallelSorter(numThreads);
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		// populate the list
		// TODO: change this implementation to test accordingly


                
		for (int i=0; i<numElements; i++) 
			list.add(i);

		// run the maximizer
		try {
			sorter.sort(list);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Finds the maximum by using <code>numThreads</code> instances of
	 * <code>ParallelSorterWorker</code> to find partial maximums and then
	 * combining the results.
	 * @param list <code>LinkedList</code> containing <code>Integers</code>
	 * @return Maximum element in the <code>LinkedList</code>
	 * @throws InterruptedException
	 */
	public LinkedList<Integer> sort(LinkedList<Integer> list) throws InterruptedException {
		//int max = Integer.MIN_VALUE; // initialize max as lowest value
		LinkedList<Integer> tempList=new LinkedList<>();
		System.out.println(numThreads);
		// run numThreads instances of ParallelSorterWorker
		for (int i=0; i < numThreads; i++) {
			workers.add(i, new ParallelSorterWorker(list));
			workers.get(i).start();
		}
                
		// wait for threads to finish
		for (int i=0; i<workers.size(); i++)
			workers.get(i).join();
		
		// take the highest of the partial maximums
		// TODO: IMPLEMENT CODE HERE
                
                for (ParallelSorterWorker w : workers) 
                {       tempList.addAll(w.getList());
                
			Collections.sort(tempList);
                        System.out.println(w.getName()+ "sorted");
                        
                }
                 //System.out.println(tempList.size());       
		return tempList;
	}
	
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HW1part1;

/**
 *
 * @author vishakha
 */


//import java.util.LinkedList;
import HW1part1.ParallelMaximizerWorker;
import java.util.*;

/**
 * This class runs <code>numThreads</code> instances of
 * <code>ParallelMaximizerWorker</code> in parallel to find the maximum
 * <code>Integer</code> in a <code>LinkedList</code>.
 */
public class ParallelMaximizer {
	
	int numThreads;
	static ArrayList<ParallelMaximizerWorker> workers; // = new ArrayList<ParallelMaximizerWorker>(numThreads);

	public ParallelMaximizer(int numThreads) {
		workers = new ArrayList<ParallelMaximizerWorker>(numThreads);
                this.numThreads=numThreads;
//                 for(int i=0;i<numThreads;i++)
//                    workers.add(null);
                
	}


	
	public static void main(String[] args) {
		int numThreads = 4; // number of threads for the maximizer
		int numElements = 10; // number of integers in the list
		
		ParallelMaximizer maximizer = new ParallelMaximizer(numThreads);
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		// populate the list
		// TODO: change this implementation to test accordingly


                
		for (int i=0; i<numElements; i++) 
			list.add(i);

		// run the maximizer
		try {
			System.out.println(maximizer.max(list));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Finds the maximum by using <code>numThreads</code> instances of
	 * <code>ParallelMaximizerWorker</code> to find partial maximums and then
	 * combining the results.
	 * @param list <code>LinkedList</code> containing <code>Integers</code>
	 * @return Maximum element in the <code>LinkedList</code>
	 * @throws InterruptedException
	 */
	public int max(LinkedList<Integer> list) throws InterruptedException {
		int max = Integer.MIN_VALUE; // initialize max as lowest value
		
		System.out.println(numThreads);
		// run numThreads instances of ParallelMaximizerWorker
		for (int i=0; i < numThreads; i++) {
			workers.add(i, new ParallelMaximizerWorker(list));
			workers.get(i).start();
		}
		// wait for threads to finish
		for (int i=0; i<workers.size(); i++)
			workers.get(i).join();
		
		// take the highest of the partial maximums
		// TODO: IMPLEMENT CODE HERE
                
                for (ParallelMaximizerWorker w : workers) 
                {
			max = Math.max(max, w.getPartialMax());
                        //System.out.println(w.getName()+":"+max);
                }
                //System.out.println(max);
		return max;
	}
	
}

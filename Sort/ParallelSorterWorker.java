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
import HW1part1.*;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Given a <code>LinkedList</code>, this class will find the maximum over a
 * subset of its <code>Integers</code>.
 */
public class ParallelSorterWorker extends Thread {

	protected LinkedList<Integer> list;
	protected LinkedList<Integer> partialList=new LinkedList<>();// initialize to lowest value
	
	public ParallelSorterWorker(LinkedList<Integer> list) {
		this.list = list;
	}
	
	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
		while (true) {
			//int number;
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized(list) {
				if (list.isEmpty())
					return; // list is empty
				//number = list.remove();
                                //System.out.println("number"+number);
                                partialList.add(list.remove());
			}
			
                          
			// update partialMax according to new value
			// TODO: IMPLEMENT CODE HERE
                        //partialMax = Math.max(number, partialMax);
                        Collections.sort(partialList);
		}
	}
	
	public LinkedList<Integer> getList() {
		return partialList;
	}

}




package p2.p2;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;
	public int currcapacity;
	public Queue<Food> listOfFood;

	//YOUR CODE GOES HERE...


	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		this.currcapacity=capacityIn;
		this.listOfFood=new LinkedList<Food>();
		
		//YOUR CODE GOES HERE...

	}
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public void makeFood(Cook cookname,int orderNumber) throws InterruptedException {
		//YOUR CODE GOES HERE...
		
			listOfFood.add(machineFoodType);
            Thread current=new Thread(new CookAnItem(cookname,orderNumber));
            current.start();
           
            
            
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		Cook currentCook;
		int currentOrderNum;
		public CookAnItem(Cook currentCook,int currentOrderNum){
			this.currentCook=currentCook;
			this.currentOrderNum=currentOrderNum;
		}
		public void run() {
			try {
                            //YOUR CODE GOES HERE...
                            Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, machineFoodType));
                            Thread.sleep(machineFoodType.cookTimeMS);
                            Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, machineFoodType));
                            Simulation.logEvent(SimulationEvent.cookFinishedFood(currentCook,machineFoodType,currentOrderNum));
                            synchronized (listOfFood) {
								listOfFood.remove();
								listOfFood.notifyAll();
								
							}
                            
                            synchronized (currentCook.foodDone) {
								currentCook.foodDone.add(machineFoodType);
								currentCook.foodDone.notifyAll();
							}

				
			} catch(InterruptedException e) { }
		}
	}
 

	public String toString() {
		return machineName;
	}
}
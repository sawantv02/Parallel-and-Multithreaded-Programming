

package p2.p2;

import java.util.LinkedList;
import java.util.List;


/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	private Customer customer;
	public List<Food> foodDone=new LinkedList<Food>();
 
	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run(){

		
		try {
			while(true) {
				//YOUR CODE GOES HERE...
				synchronized(Simulation.listOfOrders){
					while(Simulation.listOfOrders.isEmpty()){
						Simulation.listOfOrders.wait();
					}
					customer=Simulation.listOfOrders.remove();
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this,customer.getorderList(),customer.getOrderNum()));
					Simulation.listOfOrders.notifyAll();
				}
                            
                for(int i=0;i<customer.getorderList().size();i++){
                	Food food=customer.getorderList().get(i);
                	
                	if(food.equals(FoodType.burger)){
                		synchronized(Simulation.m1.listOfFood){
                			while(!(Simulation.m1.listOfFood.size()<Simulation.m1.currcapacity)){
                				Simulation.m1.listOfFood.wait();
                			}
                			Simulation.logEvent(SimulationEvent.cookStartedFood(this,FoodType.burger,customer.getOrderNum()));
                			Simulation.m1.makeFood(this,customer.getOrderNum());
                			Simulation.m1.listOfFood.notifyAll();
                		}
                	}
                	
                	if(food.equals(FoodType.fries)){
                		synchronized (Simulation.m2.listOfFood) {
                			while(!(Simulation.m2.listOfFood.size()<Simulation.m2.currcapacity)){
                				Simulation.m2.listOfFood.wait();
                			}
                			Simulation.logEvent(SimulationEvent.cookStartedFood(this,FoodType.fries,customer.getOrderNum()));
                			Simulation.m2.makeFood(this,customer.getOrderNum());
                			Simulation.m2.listOfFood.notifyAll();
						}
                	}
                	
                	if(food.equals(FoodType.coffee)){
                		synchronized (Simulation.m3.listOfFood) {
                			while(!(Simulation.m3.listOfFood.size()<Simulation.m3.currcapacity)){
                				Simulation.m3.listOfFood.wait();
                			}
                			Simulation.logEvent(SimulationEvent.cookStartedFood(this,FoodType.coffee,customer.getOrderNum()));
                			Simulation.m3.makeFood(this,customer.getOrderNum());
                			Simulation.m3.listOfFood.notifyAll();
						}
                	}
                
                }
                synchronized(foodDone) {
					while(!(foodDone.size()==customer.getorderList().size())){
						foodDone.wait();
						foodDone.notifyAll();
					}
					
				}
                
                Simulation.logEvent(SimulationEvent.cookCompletedOrder(this,customer.getOrderNum()));
                
                synchronized (Simulation.ordersDone) {
					Simulation.ordersDone.put(customer,true);
					Simulation.ordersDone.notifyAll();
					
				}   
             
                foodDone=new LinkedList<Food>();
			}
			
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}
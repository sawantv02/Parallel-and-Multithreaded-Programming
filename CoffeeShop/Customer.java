package p2.p2;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;    
	
	private static int runningCounter = 0;
	

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
	}

	public String toString() {
		return name;
	}
	
	public List<Food> getorderList(){
		return order;
	}

	public int getOrderNum(){
		return orderNum;
	}
	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		//YOUR CODE GOES HERE...
		
		synchronized (Simulation.presentCapacity) {
			while(!(Simulation.presentCapacity.size()<Simulation.events.get(0).simParams[2])){
				try{
					Simulation.presentCapacity.wait();
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}
			
			Simulation.presentCapacity.add(this);
			Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));		
			Simulation.presentCapacity.notifyAll();
		}
		
		synchronized (Simulation.listOfOrders) {
			
			Simulation.listOfOrders.add(this);
			Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
			Simulation.listOfOrders.notifyAll();
		}
		
		synchronized (Simulation.ordersDone) {
			Simulation.ordersDone.put(this,false);
		}
		synchronized (Simulation.ordersDone) {
			while(!(Simulation.ordersDone.get(this)))
			{
				try{
					Simulation.ordersDone.wait();
				}
				catch(InterruptedException e){
					e.printStackTrace();
				}
			}

			Simulation.logEvent(SimulationEvent.customerReceivedOrder(this,this.order,this.orderNum));	
			Simulation.ordersDone.notifyAll();
		}
		

		synchronized (Simulation.presentCapacity) {
			Simulation.presentCapacity.remove(this);
			Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
			Simulation.presentCapacity.notifyAll();
		}
		
		
		
	}
}
package p1;

/**
 *  @author YOUR NAME SHOULD GO HERE
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class AuctionServer
{
	/**
	 * Singleton: the following code makes the server a Singleton. You should
	 * not edit the code in the following noted section.
	 * 
	 * For test purposes, we made the constructor protected. 
	 */

	/* Singleton: Begin code that you SHOULD NOT CHANGE! */
	protected AuctionServer()
	{
	}

	private static AuctionServer instance = new AuctionServer();

	public static AuctionServer getInstance()
	{
		return instance;
	}

	/* Singleton: End code that you SHOULD NOT CHANGE! */

	/* Statistic variables and server constants: Begin code you should likely leave alone. */


	/**
	 * Server statistic variables and access methods:
	 */
	private int soldItemsCount = 0;
	private int revenue = 0;

	public int soldItemsCount()
	{
		return this.soldItemsCount;
	}

	public int revenue()
	{
            synchronized(itemsAndIDs)
            {
             synchronized(highestBids)   
             {
            for (int i : itemsAndIDs.keySet())
            {
                if (!itemsAndIDs.get(i).biddingOpen() && highestBids.containsKey(i))
                {
                    if (highestBids.get(i) != itemsAndIDs.get(i).lowestBiddingPrice())
                    {
                        this.revenue = this.revenue + highestBids.get(i);
                    }
                }
            }
		return this.revenue;
             }}
       }

	/**
	 * Server restriction constants:
	 */
	public static final int maxBidCount = 10; // The maximum number of bids at any given time for a buyer.
	public static final int maxSellerItems = 20; // The maximum number of items that a seller can submit at any given time.
	public static final int serverCapacity = 80; // The maximum number of active items at a given time.


	/* Statistic variables and server constants: End code you should likely leave alone. */



	/**
	 * Some variables we think will be of potential use as you implement the server...
	 */

	// List of items currently up for bidding (will eventually remove things that have expired).
	private List<Item> itemsUpForBidding = new ArrayList<Item>();


	// The last value used as a listing ID.  We'll assume the first thing added gets a listing ID of 0.
	private int lastListingID = -1; 

	// List of item IDs and actual items.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Item> itemsAndIDs = new HashMap<Integer, Item>();

	// List of itemIDs and the highest bid for each item.  This is a running list with everything ever added to the auction.
	private HashMap<Integer, Integer> highestBids = new HashMap<Integer, Integer>();

	// List of itemIDs and the person who made the highest bid for each item.   This is a running list with everything ever bid upon.
	private HashMap<Integer, String> highestBidders = new HashMap<Integer, String>(); 




	// List of sellers and how many items they have currently up for bidding.
	private HashMap<String, Integer> itemsPerSeller = new HashMap<String, Integer>();

	// List of buyers and how many items on which they are currently bidding.
	private HashMap<String, Integer> itemsPerBuyer = new HashMap<String, Integer>();



	// Object used for instance synchronization if you need to do it at some point 
	// since as a good practice we don't use synchronized (this) if we are doing internal
	// synchronization.
	//
	// private Object instanceLock = new Object(); 

	/*
	 *  The code from this point forward can and should be changed to correctly and safely 
	 *  implement the methods as needed to create a working multi-threaded server for the 
	 *  system.  If you need to add Object instances here to use for locking, place a comment
	 *  with them saying what they represent.  Note that if they just represent one structure
	 *  then you should probably be using that structure's intrinsic lock.
	 */
        
//Invariants: size(itemsUpForBidding) < serverCapacity, itemsPerSeller for each seller < maxSellerItems, number of bids placed by bidder < maxBidCount
//pre-condition: itemsAndIDs, itemsPerSeller, itemsUpForBidding,sellerName, maxSellerItems, itemName NOT EQUAL null,
//				 0 < lowestBiddingPrice < 99
//post-condition: result = lastListingID or -1
//exceptions: if seller submits three times an item with opening price > $75, disqualify sellerName,
//			  if five or more of seller's items expire before anybody can bid, disqualify sellerName
	/**
	 * Attempt to submit an <code>Item</code> to the auction
	 * @param sellerName Name of the <code>Seller</code>
	 * @param itemName Name of the <code>Item</code>
	 * @param lowestBiddingPrice Opening price
	 * @param biddingDurationMs Bidding duration in milliseconds
	 * @return A positive, unique listing ID if the <code>Item</code> listed successfully, otherwise -1
	 */
	public synchronized int submitItem(String sellerName, String itemName, int lowestBiddingPrice, int biddingDurationMs)
	{
           
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   Make sure there's room in the auction site.
            System.out.println(sellerName+" sells item: "+itemName+" with opening price: "+lowestBiddingPrice);
            if (this.itemsUpForBidding.size() == serverCapacity) {
                //System.out.println("Server Capacity reached.Cannot submit more items.");
                return -1;
            }
		//   If the seller is a new one, add them to the list of sellers.
            if (!itemsPerSeller.containsKey(sellerName)) {
			itemsPerSeller.put(sellerName, 1);
		}
            //   If the seller has too many items up for bidding, don't let them add this one.
		
            else {
                int numberOfItems;
                numberOfItems = itemsPerSeller.get(sellerName);
                if(numberOfItems == maxSellerItems)
                {
                    return -1;
                }
                itemsPerSeller.put(sellerName, numberOfItems + 1);
            }
            
            //opening price should be between $0 to $99
            //if (lowestBiddingPrice <= 0 && lowestBiddingPrice >99 && biddingDurationMs <= 0)
            if (lowestBiddingPrice <= 0 && lowestBiddingPrice >99 && biddingDurationMs <= 0)
            {
			return -1;
		}
//            Seller is disqualified if it submits three times an item with opening price > $75.
            int countParticularItemBid =0;
            
            if(lowestBiddingPrice >= 75)
            {
                countParticularItemBid++;
            }
            if (countParticularItemBid >= 3)
            {
                System.out.println("Above 75 "+itemName+sellerName);
                return -1;
            }
            
            
            lastListingID = lastListingID + 1;
            
           
         //   Don't forget to increment the number of things the seller has currently listed.
            Item item = new Item(sellerName, itemName, lastListingID, lowestBiddingPrice, biddingDurationMs);
		itemsAndIDs.put(lastListingID, item);
		this.itemsUpForBidding.add(item);
		highestBids.put(lastListingID, lowestBiddingPrice);
           
                 //Seller is disqualified if five or more of its items expire before anybody can bid
                int itemsExpireCount =0;
            if(!item.biddingOpen())
            {
             if(highestBids.containsKey(lastListingID))
                        {
                            if(highestBids.get(lastListingID) == lowestBiddingPrice)
                            {
                                itemsExpireCount++;
                                        
                            }
                        }
            }
            if(itemsExpireCount >= 5)
            {
                System.out.println("Five or more times an item was unbid");
                return -1;
            }
		return lastListingID;
		//return -1;
 }


//pre-condition: none
//post-condition: return list itemsUpForBidding	
	/**
	 * Get all <code>Items</code> active in the auction
	 * @return A copy of the <code>List</code> of <code>Items</code>
	 */
	public synchronized List<Item> getItems()
	{
             
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//    Don't forget that whatever you return is now outside of your control.
		ArrayList<Item> t_itemsUpForBidding = new ArrayList<>();
             
                for (Item item : itemsUpForBidding) {
                  
			if (item.biddingOpen()) {
				t_itemsUpForBidding.add(item);
			}
                    
		}
               
		return t_itemsUpForBidding;
             
            
	}

//pre-condition: itemsAndIDs &itemsUpForBidding NOT null, listingID > -1, itemsPerBuyer > -1, biddingAmount > highestbid
//post-condition: boolean result	
	/**
	 * Attempt to submit a bid for an <code>Item</code>
	 * @param bidderName Name of the <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @param biddingAmount Total amount to bid
	 * @return True if successfully bid, false otherwise
	 */
	public boolean submitBid(String bidderName, int listingID, int biddingAmount)
	{
		// TODO: IMPLEMENT CODE HERE
            Item item = null;
		// Some reminders:
		//   See if the item exists.
            synchronized(itemsAndIDs) {
                
            
            if (itemsAndIDs.containsKey(listingID)) 
            {
                item = itemsAndIDs.get(listingID);
                //   See if it can be bid upon.
                //   See if this bidder has too many items in their bidding list.
		//   Get current bidding info.
		//   See if they already hold the highest bid.
		//   See if the new bid isn't better than the existing/opening bid floor.
		//   Decrement the former winning bidder's count
		//   Put your bid in place
                synchronized(itemsUpForBidding) {
                    
                   synchronized(highestBids) {
                       
                       synchronized(itemsPerBuyer) {
           if (this.itemsUpForBidding.contains(item) && item.biddingOpen()) 
                {
                    if (biddingAmount > highestBids.get(listingID)) 
                    {
                        if (!highestBidders.containsKey(listingID) || !itemsPerBuyer.containsKey(bidderName)) 
                        {
                            itemsPerBuyer.put(bidderName, 1);
			} 
                else if (highestBidders.containsKey(listingID)&& !highestBidders.get(listingID).equals(bidderName)) 
                {
			int numberOfItems = itemsPerBuyer.get(bidderName);
			if (numberOfItems == maxBidCount) 
                        {
			return false;
			}
                        else
			itemsPerBuyer.put(bidderName, numberOfItems + 1);
		}

		highestBidders.put(listingID, bidderName);
		highestBids.put(listingID, biddingAmount);
		System.out.println("Bidder "+bidderName+" Bidding Amt "+biddingAmount+" Item "+item);
                return true;
                
	}
    }
	               	
   }
                   }}}}
		return false;
}

//pre-condition: bidderName != null, listingID > 0
//post-condition: result = 1 or 2 or 3
	/**
	 * Check the status of a <code>Bidder</code>'s bid on an <code>Item</code>
	 * @param bidderName Name of <code>Bidder</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return 1 (success) if bid is over and this <code>Bidder</code> has won<br>
	 * 2 (open) if this <code>Item</code> is still up for auction<br>
	 * 3 (failed) If this <code>Bidder</code> did not win or the <code>Item</code> does not exist
	 */
	public synchronized int checkBidStatus(String bidderName, int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		// Some reminders:
		//   If the bidding is closed, clean up for that item.
       
            
            //if listingID does not exists in the list, return FAILED
            if (!itemsAndIDs.containsKey(listingID)) 
            {
			return 3;
            }
            Item item = itemsAndIDs.get(listingID);
            
            //if bidding is closed
            if (!item.biddingOpen()) 
            {
                
                if (bidderName.equals(highestBidders.get(listingID)))
                {
                    //soldItemsCount=soldItemsCount+1;
                    //int bidderItems =0, sellerItems =0;
                    // Remove item from the list of things up for bidding.
                    soldItemsCount = soldItemsCount + 1;
                    this.itemsUpForBidding.remove(item);
                    
                    
                    //Update the number of open bids for this seller
                    int sellerItems = itemsPerSeller.get(item.seller());
                    itemsPerSeller.put(item.seller(), sellerItems - 1);
                    //Decrease the count of items being bid on by the winning bidder if there was any...
                    int bidderItems = itemsPerBuyer.get(bidderName);
                    itemsPerBuyer.put(bidderName,bidderItems - 1);
                    
                    return 1;
                }
                else
                
                    return 3;
             }
                else
            {
                return 2;
            }
                       
}
//pre-condition:none
//post-condition: return highestbid if listingID exists else return -1
	/**
	 * Check the current bid for an <code>Item</code>
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return The highest bid so far or the opening price if no bid has been made,
	 * -1 if no <code>Item</code> exists
	 */
	public int itemPrice(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
		if(listingID < 0 )
                    return -1;
                else
                {
                    synchronized (highestBids)
                    {
                        if(highestBids.containsKey(listingID))
                        {
                            return highestBids.get(listingID);
                        }
                    }
                }
               return -1; 
	}

	/**
	 * Check whether an <code>Item</code> has been bid upon yet
	 * @param listingID Unique ID of the <code>Item</code>
	 * @return True if there is no bid or the <code>Item</code> does not exist, false otherwise
	 */
	public Boolean itemUnbid(int listingID)
	{
		// TODO: IMPLEMENT CODE HERE
            synchronized(itemsAndIDs) {
              synchronized(itemsUpForBidding) {
                synchronized(highestBids) {   
              
            
            if (itemsAndIDs.containsKey(listingID)) 
            {
             Item item = itemsAndIDs.get(listingID);
             
             if(itemsUpForBidding.contains(item))
             {
                 if(item.lowestBiddingPrice() == highestBids.get(listingID))
                 {
                     return true;
                 }
                 else
                     return false;
             }
            return true;
        }
            return true;
                }}}
    }
int num =1;
        public void profit() {
		for (int i : highestBids.keySet()) {
			Item item = itemsAndIDs.get(i);
			String seller = item.seller();
			int highestBid = highestBids.get(item.listingID());
			int profit = highestBid - item.lowestBiddingPrice();
			
			if (profit > 0) {
                          
			System.out.println(num +" Item Name----"+ item.name() +" from----- "+seller +  " with Opening Price "
						+ item.lowestBiddingPrice() + " is Sold at " + highestBid + "!!! Profit: "
						+ profit);
                        num++;
				
			}
		}
	}
        
    

}
 
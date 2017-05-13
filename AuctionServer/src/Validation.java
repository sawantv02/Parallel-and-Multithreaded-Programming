/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

/**
 *
 * @author akashnagesh
 */
public class Validation {

	static Thread[] sellerThreads, bidderThreads;
	static Seller[] sellers;
	static Bidder[] bidders;

	public static void main(String args[]) throws Exception {
		int nrSellers = 10;
		int nrBidders = 100;

		sellerThreads = new Thread[nrSellers];
		bidderThreads = new Thread[nrBidders];
		sellers = new Seller[nrSellers];
		bidders = new Bidder[nrBidders];

		// Start the sellers
		for (int i = 0; i < nrSellers; ++i) {
			sellers[i] = new Seller(AuctionServer.getInstance(), "Seller" + i, 100, 50, i);
			sellerThreads[i] = new Thread(sellers[i]);
			sellerThreads[i].start();
		}

		// Start the buyers
		for (int i = 0; i < nrBidders; ++i) {
			bidders[i] = new Bidder(AuctionServer.getInstance(), "Buyer" + i, 1000, 20, 150, i);
			bidderThreads[i] = new Thread(bidders[i]);
			bidderThreads[i].start();
		}

		// Join on the sellers
		for (int i = 0; i < nrSellers; ++i) {
			try {
				sellerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Join on the bidders
		for (int i = 0; i < nrBidders; ++i) {
			try {
				bidderThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Test code

		// running test cases
		System.out.println("=========================================");
		org.junit.runner.JUnitCore.main("p1.Validation$TestCases");

	}

	public static class TestCases {
		final AuctionServer ac = AuctionServer.getInstance();

		final int soldItemCount = (Integer) getFieldValue("soldItemsCount", ac);
		final int revenue = (Integer) getFieldValue("revenue", ac);
		final int lastListingID = (Integer) getFieldValue("lastListingID", ac);

		final Map<Integer, Integer> highestBids = (HashMap) getFieldValue("highestBids", ac);
		final Map<String, Integer> itemsPerSeller = (HashMap) getFieldValue("itemsPerSeller", ac);
		final List<Item> itemsUpForBidding = (List) getFieldValue("itemsUpForBidding", ac);
		final Map<Integer, Item> itemsAndIDs = (HashMap) getFieldValue("itemsAndIDs", ac);
		final Map<Integer, String> highestBidders = (HashMap) getFieldValue("highestBidders", ac);
		final Map<String, Integer> itemsPerBuyer = (HashMap) getFieldValue("itemsPerBuyer", ac);

		// 2
		@Test
		public void revenuTest() {
			int sumOfCashSpent = Arrays.stream(bidders).mapToInt(b -> b.cashSpent()).sum();
			System.err.println(sumOfCashSpent + " " + revenue + "revenue");
			assertEquals("Sum of cash spent by bidders not equal to revenue", revenue, sumOfCashSpent);
		}

		// 3
		@Test
		public void highestbidssum() {
			final int sum = highestBids.entrySet().stream().mapToInt(es -> es.getValue()).sum();
			System.err.println(sum + " " + revenue);
			assertEquals("revenue is not equal to sum of highest bids", revenue, sum);
		}

		// 4
		@Test
		public void numberOfItemsSoldTest() {
			assertEquals("soldItemsCount = highestBidders.size()", soldItemCount, highestBidders.size());
		}

		// 5
		@Test
		public void itemsAndIdsTest() {
			assertEquals("ItemsAndIds.size() = lastListingID - 1 not equal", itemsAndIDs.size() - 1, lastListingID);
		}

		// itemsAndIds.size() = total items submitted by sellers

		// 6
		@Test
		public void itemsCount() {
			int items = itemsPerSeller.entrySet().stream().mapToInt(e -> e.getValue()).sum();
			assertEquals("itemsUpForBidding.size() != total items submitted by sellers", items,
					itemsUpForBidding.size());
		}

		// 7
		@Test
		public void maxSellerItems() {

			for (Entry<String, Integer> e : itemsPerSeller.entrySet()) {
				boolean isLessThan = e.getValue() <= 20;
				assertEquals("failed maxSeller items maintained by AuctionServer", true, isLessThan);
			}

		}

		// 8
		@Test
		public void maxBidCount() {

			int sum = 0;
			for (Entry<String, Integer> e : itemsPerBuyer.entrySet()) {
				sum += e.getValue();
			}
			boolean isLessThan = sum <= 10;
			assertEquals("failed maxBid counts items maintained by AuctionServer", true, isLessThan);
		}

		// 9
		@Test
		public void serverCapacityMaintained() {
			boolean isLessThanServerCapacity = itemsUpForBidding.size() <= 80;
			// System.err.println("========================" +
			// itemsUpForBidding.size());
			assertEquals("Server capacity not maintained", true, isLessThanServerCapacity);
		}
	}

	private static Object getFieldValue(String fieldName, Object instance) {
		Class<?> cls = instance.getClass();
		for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
			try {
				final Field field = c.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(instance);
			} catch (final NoSuchFieldException e) {
				// Try parent
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot access field " + cls.getName() + "." + fieldName, e);
			}
		}
		throw new IllegalArgumentException("Cannot find field " + cls.getName() + "." + fieldName);
	}

}
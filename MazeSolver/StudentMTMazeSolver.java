package hw7;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * This file needs to hold your solver to be tested. You can alter the class to
 * extend any class that extends MazeSolver. It must have a constructor that
 * takes in a Maze. It must have a solve() method that returns the datatype List
 * <Direction> which will either be a reference to a list of steps to take or
 * will be null if the maze cannot be solved.
 */
public class StudentMTMazeSolver extends SkippingMazeSolver {
	public ExecutorService thPool;
	public boolean solved;
	public int count;

	public StudentMTMazeSolver(Maze maze) {
		super(maze);
		solved=false;
		count=0;
	}

	// Callable
	public class MyTask implements Callable<List<Direction>> {

		//
		private Choice fakeFirstChoice;
		//private Choice myChoice;

		public MyTask(Choice fakeFirstChoice) {
			super();
			this.fakeFirstChoice = fakeFirstChoice;
			//this.myChoice = myChoice;
		}

		@Override
		public List<Direction> call() throws SolutionFound {
			// TODO Auto-generated method stub

			LinkedList<Choice> choiceStack = new LinkedList<Choice>();
			//
			Choice ch;

			try {
				choiceStack.push(fakeFirstChoice);
				//choiceStack.push(myChoice);
				//

				while (!choiceStack.isEmpty()) {
					ch = choiceStack.peek();
					if (ch.isDeadend()) {
						// backtrack.
						choiceStack.pop();
						count++;
						if (!choiceStack.isEmpty())
							choiceStack.peek().choices.pop();
						// if this item ch is deadend, as ch is peek() of
						// choices of the item under it,
						// this choice has to be removed from the choices.
						continue;
					}
					// not dead end:
					choiceStack.push(follow(ch.at, ch.choices.peek()));
				} // 逐个choice检查
					// No solution found.
				return null;
			} catch (SolutionFound e) {
				Iterator<Choice> iter = choiceStack.iterator();
				LinkedList<Direction> solutionPath = new LinkedList<Direction>();
				while (iter.hasNext()) {
					ch = iter.next();
					solutionPath.push(ch.choices.peek());// first choices in a
															// line: what are in
															// the stack
				}

				if (maze.display != null)
					maze.display.updateDisplay();
				//
				solved=true;
				//thPool.shutdownNow();
				count+=solutionPath.size();
				
				return pathToFullPath(solutionPath);
			}
		}

	}

	public List<Direction> solve() {
		int numCPU = Runtime.getRuntime().availableProcessors();
		thPool = Executors.newFixedThreadPool(numCPU + 1);
		List<Future<List<Direction>>> futures=new ArrayList<>();
		
		
		Choice firstchoice;
		try {
			firstchoice = firstChoice(maze.getStart());
		} catch (SolutionFound e) {
			
			return pathToFullPath(new ArrayList<Direction>()) ;
		}
		
		
		Deque<Direction> firstChoices=firstchoice.choices;
		//System.out.println(firstChoices.size());
		int size=firstChoices.size();
		
		for(int i=0; i<size; i++){
			Direction dir=firstChoices.pop();
			LinkedList<Direction> fakeFirstChoices=new LinkedList<Direction>();
			fakeFirstChoices.push(dir);
			
			
			Choice fakefirstchoice=new Choice(firstchoice.at, firstchoice.from, fakeFirstChoices);
			
			MyTask ta=new MyTask(fakefirstchoice);
			Future<List<Direction>> fu=thPool.submit(ta);	
			
			futures.add(fu);
			//System.out.println(i);
		}
		
		//List<Future<List<Direction>>> futures = executorService.invokeAll(callables);
		//System.out.println(futures.size());
		for(Future<List<Direction>> fu: futures){
			
			try {
				if(fu.get()!=null){
					System.out.println("count of choices made: "+count);
					return fu.get();
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
		
	}
}

package com.skillsup.lockfree.list;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class LockFreeListTest {
	private final static int SIZE = 10000;

	@Test
	public void test() {
		List<String> testList = new LockFreeListV2<>();
		String testString = null;
		for (int counter = 0; counter < SIZE; counter++) {
			testString = "Test + " + counter;
			testList.add(testString);
			Assert.assertEquals(testString, testList.get(counter));
			Assert.assertEquals(counter + 1, testList.size());
		}
		Assert.assertEquals(SIZE, testList.size());
		Assert.assertEquals("Test + 50", testList.set(50, testString));
		Assert.assertEquals(testString, testList.get(50));
		Assert.assertEquals(SIZE, testList.size());
	}

	/*    @Test
	    public void test2() throws ExecutionException, InterruptedException {
	        final List<String> testList = new LockFreeList<>();
	        ExecutorService executorService = Executors.newCachedThreadPool();
	        Future<String> threadOne = executorService.submit(new Callable<String>() {
	            @Override
	            public String call() throws Exception {
	                int expectedSize = SIZE;
	                String testString;
	                for (int counter = 0; counter < expectedSize; counter++) {
	                    testString = "Test + " + Thread.currentThread().getId();
	                    testList.add(testString);
	                }
	                return "done";
	            }
	        });
	        Future<String> threadTwo = executorService.submit(new Callable<String>() {
	            @Override
	            public String call() throws Exception {
	                int expectedSize = SIZE;
	                String testString;
	                for (int counter = 0; counter < expectedSize; counter++) {
	                    testString = "Test + " + Thread.currentThread().getId();
	                    testList.add(testString);
	                }
	                return "done";
	            }
	        });

	        while (!threadOne.isDone() && !threadTwo.isDone()){
	            System.out.println("Thread 1 and 2");
	        }
	        Assert.assertEquals("done", threadOne.get());
	        Assert.assertEquals("done", threadTwo.get());
	        Assert.assertEquals(2 * SIZE, testList.size());
	    }*/
}

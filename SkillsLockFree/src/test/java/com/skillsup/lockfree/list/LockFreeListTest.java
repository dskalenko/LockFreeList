package com.skillsup.lockfree.list;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LockFreeListTest {
    private final static int SIZE = 10000;

    @Test
    public void testAdd() throws NoSuchFieldException, IllegalAccessException {
        List<String> testList = new LockFreeList<>();
        AtomicReferenceArray<AtomicReferenceArray<String>> memory = (AtomicReferenceArray<AtomicReferenceArray<String>>) getValue(testList, "memory");
        Assert.assertEquals(memory.length(), 32);
		String testString = null;
		for (int counter = 0; counter < 1_000_000; counter++) {
			testString = "Test + " + counter;
			testList.add(testString);
			Assert.assertEquals(testString, testList.get(counter));
			Assert.assertEquals(counter + 1, testList.size());
		}
    }

    private Object getValue(Object obj, String value) throws NoSuchFieldException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(value);
        f.setAccessible(true);
        return f.get(obj);
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

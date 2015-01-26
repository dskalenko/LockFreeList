package com.skillsup.lockfree.list;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LockFreeListTest {
    private final static int SIZE = 1_000_000;

    @Test
    public void testAddNotNull() throws NoSuchFieldException, IllegalAccessException {
        List<String> testList = new LockFreeList<>();
        AtomicReferenceArray<AtomicReferenceArray<String>> memory = (AtomicReferenceArray<AtomicReferenceArray<String>>) getValue(testList, "memory");
        Assert.assertEquals(memory.length(), 32);
        for (int counter = 0; counter < SIZE; counter++) {
            String testString = "Test + " + counter;
            testList.add(testString);
            int numberOfBucket = getNumberOfBucket(counter);
            AtomicReferenceArray<String> bucket = memory.get(numberOfBucket);
            Assert.assertEquals(getBucketSize(numberOfBucket), bucket.length());
            Assert.assertEquals(testString, bucket.get(getIndexInBucket(counter)));
        }
    }

    @Test
    public void testAddNull() throws NoSuchFieldException, IllegalAccessException {
        List<String> testList = new LockFreeList<>();
        AtomicReferenceArray<AtomicReferenceArray<String>> memory = (AtomicReferenceArray<AtomicReferenceArray<String>>) getValue(testList, "memory");
        Assert.assertEquals(memory.length(), 32);
        for (int counter = 0; counter < SIZE; counter++) {
            String testString = null;
            testList.add(testString);
            int numberOfBucket = getNumberOfBucket(counter);
            AtomicReferenceArray<String> bucket = memory.get(numberOfBucket);
            Assert.assertEquals(getBucketSize(numberOfBucket), bucket.length());
            Assert.assertEquals(testString, bucket.get(getIndexInBucket(counter)));
        }
    }

    @Test
    public void testAddNullAndNotNull() throws NoSuchFieldException, IllegalAccessException {
        List<String> testList = new LockFreeList<>();
        AtomicReferenceArray<AtomicReferenceArray<String>> memory = (AtomicReferenceArray<AtomicReferenceArray<String>>) getValue(testList, "memory");
        Assert.assertEquals(memory.length(), 32);
        for (int counter = 0; counter < SIZE; counter++) {
            String testString = counter % 2 == 0 ? null : "testString" + counter;
            testList.add(testString);
            int numberOfBucket = getNumberOfBucket(counter);
            AtomicReferenceArray<String> bucket = memory.get(numberOfBucket);
            Assert.assertEquals(getBucketSize(numberOfBucket), bucket.length());
            Assert.assertEquals(testString, bucket.get(getIndexInBucket(counter)));
        }
    }

    @Test
    public void testSize() throws NoSuchFieldException, IllegalAccessException {
        List<String> testList = new LockFreeList<>();
        for (int counter = 0; counter < SIZE; counter++) {
            String testString = "testString" + counter;
            Assert.assertEquals(counter, testList.size());
            testList.add(testString);
            Assert.assertEquals(counter + 1, testList.size());
            testList.set(counter, testString);
            Assert.assertEquals(counter + 1, testList.size());
        }
        Assert.assertEquals(SIZE, testList.size());
    }

    private int getBucketSize(int numberOfBucket) {
        return 2 << numberOfBucket;
    }

    private int getNumberOfBucket(int position) {
        int pos = position + 2;
        return (Integer.numberOfTrailingZeros(Integer.highestOneBit(pos)) - 1);
    }

    private int getIndexInBucket(int position) {
        int pos = position + 2;
        return (Integer.highestOneBit(pos) ^ pos);
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

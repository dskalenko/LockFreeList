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
		AtomicReferenceArray<AtomicReferenceArray<String>> memory =
				(AtomicReferenceArray<AtomicReferenceArray<String>>) getValue(testList, "memory");
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
		AtomicReferenceArray<AtomicReferenceArray<String>> memory =
				(AtomicReferenceArray<AtomicReferenceArray<String>>) getValue(testList, "memory");
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
		AtomicReferenceArray<AtomicReferenceArray<String>> memory =
				(AtomicReferenceArray<AtomicReferenceArray<String>>) getValue(testList, "memory");
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

	@Test
	public void testGet() throws NoSuchFieldException, IllegalAccessException {
		AtomicReferenceArray<AtomicReferenceArray<String>> memory = new AtomicReferenceArray<>(32);
		int totalSize = 0;
		for (int bucketNumber = 0; bucketNumber < 15; bucketNumber++) {
			int bucketSize = getBucketSize(bucketNumber);
			AtomicReferenceArray<String> bucket = new AtomicReferenceArray<>(bucketSize);
			for (int indexInBucket = 0; indexInBucket < bucketSize; indexInBucket++) {
				String testString = "Test + " + bucketSize + "_" + indexInBucket;
				bucket.set(indexInBucket, testString);
			}
			totalSize += bucketSize;
			memory.set(bucketNumber, bucket);
		}
		List<String> testList = new LockFreeList<>();
		setValue(testList, "memory", memory);
		for (int count = 0; count < totalSize; count++) {
			String testString = "Test + " + getBucketSize(getNumberOfBucket(count)) + "_" + getIndexInBucket(count);
			Assert.assertEquals(testString, testList.get(count));
		}
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetWithIncorrectIndex() {
		List<String> testList = new LockFreeList<>();
		testList.set(1, "Test");
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

	private void setValue(Object obj, String value, Object fieldValue) throws NoSuchFieldException, IllegalAccessException {
		Field f = obj.getClass().getDeclaredField(value);
		f.setAccessible(true);
		f.set(obj, fieldValue);
	}
}

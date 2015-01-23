package com.skillsup.lockfree.list;

import java.util.AbstractList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LockFreeList<E> extends AbstractList<E> {

	private AtomicReference<Descriptor> descriptor;

	private AtomicReferenceArray[] array;

	public LockFreeList() {
		descriptor = new AtomicReference<>(new Descriptor(new WriteOperation(null, 0, false), 0));
		array = new AtomicReferenceArray[32];
		array[0] = new AtomicReferenceArray<>(2);
	}

	@Override
	public E get(int index) {
		int indexInBucket = getIndexInBucket(index);
		AtomicReferenceArray<E> bucket = getBucket(index);
		return bucket.get(indexInBucket);
	}

	@Override
	public int size() {
		Descriptor currentDesc = this.descriptor.get();
		int size = currentDesc.size;
		if (currentDesc.writeOperation.pending) {
			size = size - 1;
		}
		return size;
	}

	@Override
	public boolean add(E element) {
		Descriptor currentDesc;
		Descriptor nextDescriptor;
		do {
			currentDesc = this.descriptor.get();
			currentDesc.writeOperation.completeWrite();
			int nextSize = currentDesc.size + 1;
			ensureCapacity(nextSize);
			nextDescriptor = new Descriptor(new WriteOperation(element, currentDesc.size), nextSize);
		}
		while (!this.descriptor.compareAndSet(currentDesc, nextDescriptor));
		nextDescriptor.writeOperation.completeWrite();
		return true;
	}

	@Override
	public E set(int index, E newValue) {
		E oldValue = this.get(index);
		WriteOperation writeOperation = new WriteOperation(oldValue, newValue, index, true);
		writeOperation.completeWrite();
		return oldValue;
	}

	private void ensureCapacity(int minCapacity) {
		int bucketNumber = getNumberOfBucket(minCapacity);
		if (array[bucketNumber] == null) {
			array[bucketNumber] = new AtomicReferenceArray<>(getBucketSize(minCapacity));
		}

	}

	private int getBucketSize(int position) {
		int pos = position + 2;
		return Integer.highestOneBit(pos);
	}

	private int getIndexInBucket(int position) {
		int pos = position + 2;
		return (Integer.highestOneBit(pos) ^ pos);
	}

	private int getNumberOfBucket(int position) {
		int pos = position + 2;
		return (Integer.numberOfTrailingZeros(Integer.highestOneBit(pos)) - 1);
	}

	private AtomicReferenceArray<E> getBucket(int position) {
		int bucketNumber = getNumberOfBucket(position);
		return array[bucketNumber];
	}

	private class Descriptor {
		private final WriteOperation writeOperation;
		private final int size;

		private Descriptor(WriteOperation writeOperation, int size) {
			this.writeOperation = writeOperation;
			this.size = size;
		}
	}

	private class WriteOperation {
		private volatile boolean pending;
		private final E oldValue;
		private final E newValue;
		private final int position;

		public WriteOperation(E newValue, int position) {
			this(newValue, position, true);
		}

		public WriteOperation(E newValue, int position, boolean pending) {
			this(null, newValue, position, pending);
		}

		public WriteOperation(E oldValue, E newValue, int position, boolean pending) {
			this.pending = pending;
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.position = position;
		}

		public void completeWrite() {
			if (this.pending) {
				int indexInBucket = getIndexInBucket(this.position);
				AtomicReferenceArray<E> bucket = getBucket(this.position);
				bucket.compareAndSet(indexInBucket, this.oldValue, this.newValue);
				this.pending = false;
			}

		}
	}
}

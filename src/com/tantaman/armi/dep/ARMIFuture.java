package com.tantaman.armi.dep;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.tantaman.commons.concurrent.executors.AbstractObservableFuture;

public class ARMIFuture<V> extends AbstractObservableFuture<V> {
	public static final ARMIFuture COMPLETED_FUTURE = new ARMIFuture() {
		public boolean isDone() { return true; }
		public boolean cancel(boolean mayInterruptIfRunning) { return true; };
		public boolean isCancelled() { return false; };
		public Object get() throws InterruptedException, ExecutionException { return null; }
		public Object get(long timeout, TimeUnit unit) throws InterruptedException ,ExecutionException ,TimeoutException { return null; };
	};
	
	private static enum State {
		RUNNING,
		CANCELLED,
		COMPLETE;
	}
	
	private final ReentrantLock lock;
	private final Condition done;
	private volatile State state;
	private V value;
	
	public ARMIFuture() {
		lock = new ReentrantLock();
		done = lock.newCondition();
		value = null;
		state = State.RUNNING;
	}
	
	protected void operationCompleted(V value) {
		lock.lock();
		try {
			this.value = value;
			state = State.COMPLETE;
			done.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// remove callback
		return false;
	}
	
	@Override
	public V get() throws InterruptedException, ExecutionException {
		try {
			return get(-1, null);
		} catch (TimeoutException e) {
			e.printStackTrace();
			// won't happen
			return null;
		}
	}
	
	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		if (state == State.COMPLETE) {
			return value;
		}
		
		lock.lock();
		try {
			if (timeout != -1) {
				boolean timedOut = done.await(timeout, unit);
				if (timedOut)
					throw new TimeoutException();
			} else {
				done.await();
			}
		} finally {
			lock.unlock();
		}
		
		return value;
	}
	
	@Override
	public boolean isCancelled() {
		return state == State.CANCELLED;
	}
	
	@Override
	public boolean isDone() {
		return state == State.COMPLETE;
	}
}

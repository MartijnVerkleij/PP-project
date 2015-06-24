package grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Locks {

	Map<String, Lock> locks = new HashMap<String, Lock>();
	
	public boolean registerLock(String id) {
		if (locks.containsKey(id)) {
			return false;
		} else {
			locks.put(id, new Lock());
			return true;
		}
	}
	
	/**
	 * Attempts to acquire the lock on the lock with a given ID.
	 * It does NOT report whether a lock with that ID actually
	 * exists, it will simply return false if there is none.
	 * @param id name of lock that we want to acquire.
	 * @return <code>true</code> if it was acquired successfully. 
	 * <code>false</code> if the lock was not successfully acquired, 
	 * or if the lock dies not exist at all.
	 */
	
	public boolean acquireLock(String id) {
		if (locks.containsKey(id)) {
			return locks.get(id).acquire();
		} else {
			return false;
		}
	}
	
	/**
	 * Releases the lock on a lock with the given ID, if it exists. 
	 * @param id lock to release.
	 */
	public void releaseLock(String id) {
		if (locks.containsKey(id)) {
			locks.get(id).release();
		}
	}
	
	
	/**
	 * Simple implementation of a reentrant lock.
	 * @author martijn
	 *
	 */
	
	public class Lock {
		boolean locked;
		
		/**
		 * Reentrant lock. 
		 */
		public Lock() {
			locked = false;			
		}
		
		/**
		 * This function tries to acquire the lock of this object. 
		 * It returns whether it successfully acquired the lock.
		 * When this function returns <code>true</code> the lock
		 * is now held.
		 * @return 
		 */
		public synchronized boolean acquire() {
			if (locked) {
				return false;
			} else {
				locked = true;
				return true;
			}
		}
		
		/**
		 * Releases the lock held on this object.
		 */
		public synchronized void release() {
			locked = false;
		}
		
		/**
		 * Returns whether it is locked at the moment it is called.
		 * @return
		 */
		public synchronized boolean isLocked() {
			return locked;
		}
	}
}

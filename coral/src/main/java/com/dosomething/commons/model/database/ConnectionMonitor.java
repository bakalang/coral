package com.dosomething.commons.model.database;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConnectionMonitor {

	private static final ConnectionMonitor instance = new ConnectionMonitor();
	private ConcurrentHashMap<Long, ConnectionWrapper> connestions = new ConcurrentHashMap<Long, ConnectionWrapper>();
	private AtomicLong atomic = new AtomicLong(0);

	private ConnectionMonitor() {
		super();
		try {
			Thread t = new MonitorThread();
			t.setDaemon(true);
			t.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public final static ConnectionMonitor getInstance() {

		return instance;
	}

	public void addConnection(ConnectionWrapper conn) {
		connestions.put(conn.getSeq(), conn);
	}

	public void removeConnection(ConnectionWrapper conn) {
		connestions.remove(conn.getSeq());
	}

	public long getAtomicInteger() {
		return atomic.incrementAndGet();
	}

	public void checkConnection() {
		long ts = System.currentTimeMillis();
		if(connestions.size() == 0) {
			return;
		}
		Set<Long> removedConnectionWrapper = null;
		for (ConnectionWrapper conn : connestions.values()) {
			//執行超過五秒顯示
			if (ts - conn.getTimeStamp() > 5000) {
				//LogUtils.cfbook.error("connection not close over "
				//	+ (System.currentTimeMillis() - conn.getTimeStamp()) + " ms : " + conn.getSql() + "\n" + conn.getStackTrace());
			}
			//超過一分鐘就不再顯示了
			if (ts - conn.getTimeStamp() > 60000) {
				if(removedConnectionWrapper == null) {
					removedConnectionWrapper = new HashSet<Long>();
				}
				removedConnectionWrapper.add(conn.getSeq());
			}
		}
		if(removedConnectionWrapper != null) {
			for(Long seq : removedConnectionWrapper) {
				connestions.remove(seq);
			}
		}
	}
}

class MonitorThread extends Thread {

	public MonitorThread() {
	}

	public void run() {

		while (true) {
			try {
				Thread.currentThread().sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				ConnectionMonitor.getInstance().checkConnection();
			} catch (Exception ex1) {
				ex1.printStackTrace(System.out);
			} finally {

			}

		}
	}

}
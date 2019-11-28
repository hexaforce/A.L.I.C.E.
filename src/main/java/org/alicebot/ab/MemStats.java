package org.alicebot.ab;

import org.alicebot.ab.utils.MemoryUtils;

public class MemStats {
	public static long prevHeapSize = 0L;
	public static void memStats() {
		long heapSize = MemoryUtils.totalMemory();
		long heapMaxSize = MemoryUtils.maxMemory();
		long heapFreeSize = MemoryUtils.freeMemory();
		long diff = heapSize - prevHeapSize;
		prevHeapSize = heapSize;
		System.out.println("Heap " + heapSize + " MaxSize " + heapMaxSize + " Free " + heapFreeSize + " Diff " + diff);
	}
}

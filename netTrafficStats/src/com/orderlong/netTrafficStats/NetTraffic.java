package com.orderlong.netTrafficStats;

public class NetTraffic {

	static {
		System.loadLibrary("nettrafficstats");
	}
	/**
     * The return value to indicate that the device does not support the statistic.
     */
    public final static int UNSUPPORTED = -1;

    /**
     * Get the total number of packets transmitted through the mobile interface.
     *
     * @return number of packets.  If the statistics are not supported by this device,
     * {@link #UNSUPPORTED} will be returned.
     */
    public static native long getMobileTxPackets();

    /**
     * Get the total number of packets received through the mobile interface.
     *
     * @return number of packets.  If the statistics are not supported by this device,
     * {@link #UNSUPPORTED} will be returned.
     */
    public static native long getMobileRxPackets();

    /**
     * Get the total number of bytes transmitted through the mobile interface.
     *
     * @return number of bytes.  If the statistics are not supported by this device,
     * {@link #UNSUPPORTED} will be returned.
     */
      public static native long getMobileTxBytes();

    /**
     * Get the total number of bytes received through the mobile interface.
     *
     * @return number of bytes.  If the statistics are not supported by this device,
     * {@link #UNSUPPORTED} will be returned.
     */
    public static native long getMobileRxBytes();

    /**
     * Get the total number of packets sent through all network interfaces.
     *
     * @return the number of packets.  If the statistics are not supported by this device,
     * {@link #UNSUPPORTED} will be returned.
     */
    public static native long getTotalTxPackets();

    /**
     * Get the total number of packets received through all network interfaces.
     *
     * @return number of packets.  If the statistics are not supported by this device,
     * {@link #UNSUPPORTED} will be returned.
     */
    public static native long getTotalRxPackets();

    /**
     * Get the total number of bytes sent through all network interfaces.
     *
     * @return number of bytes.  If the statistics are not supported by this device,
     * {@link #UNSUPPORTED} will be returned.
     */
    public static native long getTotalTxBytes();

    /**
     * Get the total number of bytes received through all network interfaces.
     *
     * @return number of bytes.  If the statistics are not supported by this device,
     * {@link #UNSUPPORTED} will be returned.
     */
    public static native long getTotalRxBytes();

    /**
     * Get the number of bytes sent through the network for this UID.
     * The statistics are across all interfaces.
     *
     * {@see android.os.Process#myUid()}.
     *
     * @param uid The UID of the process to examine.
     * @return number of bytes.  If the statistics are not supported by this device,
     * {@link #UNSUPPORTED} will be returned.
     */
    public static native long getUidTxBytes(int uid);

    /**
     * Get the number of bytes received through the network for this UID.
     * The statistics are across all interfaces.
     *
     * {@see android.os.Process#myUid()}.
     *
     * @param uid The UID of the process to examine.
     * @return number of bytes
     */
    public static native long getUidRxBytes(int uid);
	
	
}

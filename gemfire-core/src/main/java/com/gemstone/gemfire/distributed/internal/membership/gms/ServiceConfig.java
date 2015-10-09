package com.gemstone.gemfire.distributed.internal.membership.gms;

import java.net.InetAddress;

import com.gemstone.gemfire.distributed.Locator;
import com.gemstone.gemfire.distributed.internal.DistributionConfig;
import com.gemstone.gemfire.internal.SocketCreator;
import com.gemstone.gemfire.internal.admin.remote.RemoteTransportConfig;

public class ServiceConfig {
  /** various settings from Geode configuration */
  private int joinTimeout;
  private int[] membershipPortRange;
  private int udpRecvBufferSize;
  private int udpSendBufferSize;
  private long memberTimeout;
  private Integer lossThreshold;
  private Integer memberWeight;
  private boolean networkPartitionDetectionEnabled;
  private int locatorWaitTime;

  /** the configuration for the distributed system */
  private DistributionConfig dconfig;
  
  /** the transport config from the distribution manager */
  private RemoteTransportConfig transport;


  public int getLocatorWaitTime() {
    return locatorWaitTime;
  }


  public int getJoinTimeout() {
    return joinTimeout;
  }


  public int[] getMembershipPortRange() {
    return membershipPortRange;
  }


  public int getUdpRecvBufferSize() {
    return udpRecvBufferSize;
  }


  public int getUdpSendBufferSize() {
    return udpSendBufferSize;
  }


  public long getMemberTimeout() {
    return memberTimeout;
  }


  public int getLossThreshold() {
    return lossThreshold;
  }


  public int getMemberWeight() {
    return memberWeight;
  }


  public boolean isNetworkPartitionDetectionEnabled() {
    return networkPartitionDetectionEnabled;
  }
  
  public void setNetworkPartitionDetectionEnabled(boolean enabled) {
    this.networkPartitionDetectionEnabled = enabled;
  }
  
  /**
   * returns the address that will be used by the DirectChannel to
   * identify this member
   */
  public InetAddress getInetAddress() {
    String bindAddress = this.dconfig.getBindAddress();

    try {
      /* note: had to change the following to make sure the prop wasn't empty 
         in addition to not null for admin.DistributedSystemFactory */
      if (bindAddress != null && bindAddress.length() > 0) {
        return InetAddress.getByName(bindAddress);

      }
      else {
       return SocketCreator.getLocalHost();
      }
    }
    catch (java.net.UnknownHostException unhe) {
      throw new RuntimeException(unhe);

    }
  }
  
  public DistributionConfig getDistributionConfig() {
    return this.dconfig;
  }


  public RemoteTransportConfig getTransport() {
    return this.transport;
  }
  

  public ServiceConfig(RemoteTransportConfig transport, DistributionConfig theConfig) {
    this.dconfig = theConfig;
    this.transport = transport;
    
    int defaultJoinTimeout = 17000;
    if (theConfig.getLocators().length() > 0 && !Locator.hasLocators()) {
      defaultJoinTimeout = 60000;
    }
    joinTimeout = Integer.getInteger("p2p.joinTimeout", defaultJoinTimeout).intValue();
    
    // if network partition detection is enabled, we must connect to the locators
    // more frequently in order to make sure we're not isolated from them
    if (theConfig.getEnableNetworkPartitionDetection()) {
      if (!SocketCreator.FORCE_DNS_USE) {
        SocketCreator.resolve_dns = false;
      }
    }


    membershipPortRange = theConfig.getMembershipPortRange();
    
    udpRecvBufferSize = DistributionConfig.DEFAULT_UDP_RECV_BUFFER_SIZE_REDUCED;
    udpSendBufferSize = theConfig.getUdpSendBufferSize();

    memberTimeout = theConfig.getMemberTimeout();

    // The default view-ack timeout in 7.0 is 12347 ms but is adjusted based on the member-timeout.
    // We don't want a longer timeout than 12437 because new members will likely time out trying to 
    // connect because their join timeouts are set to expect a shorter period
    int ackCollectionTimeout = theConfig.getMemberTimeout() * 2 * 12437 / 10000;
    if (ackCollectionTimeout < 1500) {
      ackCollectionTimeout = 1500;
    } else if (ackCollectionTimeout > 12437) {
      ackCollectionTimeout = 12437;
    }
    ackCollectionTimeout = Integer.getInteger("gemfire.VIEW_ACK_TIMEOUT", ackCollectionTimeout).intValue();

    lossThreshold = Integer.getInteger("gemfire.network-partition-threshold", 51);
    if (lossThreshold < 51) lossThreshold = 51;
    if (lossThreshold > 100) lossThreshold = 100;
    
    memberWeight = Integer.getInteger("gemfire.member-weight", 0);
    locatorWaitTime = theConfig.getLocatorWaitTime();
    
    networkPartitionDetectionEnabled = theConfig.getEnableNetworkPartitionDetection();
  }

}

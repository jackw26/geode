/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.geode.management.internal.configuration.mutators;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.ObjectName;

import com.sun.xml.bind.v2.TODO;
import org.apache.commons.lang3.NotImplementedException;

import org.apache.geode.annotations.VisibleForTesting;
import org.apache.geode.cache.configuration.CacheConfig;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.DistributedSystemMXBean;
import org.apache.geode.management.GatewayReceiverMXBean;
import org.apache.geode.management.ManagementService;
import org.apache.geode.management.configuration.GatewayReceiverConfig;
import org.apache.geode.management.internal.MBeanJMXAdapter;
import org.apache.geode.management.internal.SystemManagementService;

public class GatewayReceiverConfigManager implements ConfigurationManager<GatewayReceiverConfig> {
  final private InternalCache cache;

  @VisibleForTesting
  GatewayReceiverConfigManager() {
    cache = null;
  }

  public GatewayReceiverConfigManager(InternalCache cache) {
    this.cache = cache;
  }

  @Override
  public void add(GatewayReceiverConfig config, CacheConfig existing) {
    throw new NotImplementedException("Not implemented");
  }

  @Override
  public void update(GatewayReceiverConfig config, CacheConfig existing) {
    throw new NotImplementedException("Not implemented");
  }

  @Override
  public void delete(GatewayReceiverConfig config, CacheConfig existing) {
    throw new NotImplementedException("Not implemented");
  }

  @Override
  public List<GatewayReceiverConfig> list(GatewayReceiverConfig filterConfig,
                                           CacheConfig existing) {
    // Get gatewayreceiverbeans
//    DistributedSystemMXBean dMXbean = this.getSystemManagementService().getDistributedSystemMXBean();
//
//    List<GatewayReceiverMXBean> GatewayReceiverMXBeans = getGatewayReceiverMXBeans(dMXbean, ?)//List<DistributedMember> members);
    // Transform gatewayreceiver beans into RuntimeGatewayReceiverConfig
//    return existing.getGatewayReceiver().
    Set<DistributedMember> distributedMembers = getDistributedMembers(filterConfig);
    return null;
  }

 //TODO: we stole this from memberconfigmanager we should find a way to make it available for both.
  @VisibleForTesting
  Set<DistributedMember> getDistributedMembers(GatewayReceiverConfig filter) {
    Set<InternalDistributedMember> distributionManagerIds =
            cache.getDistributionManager().getDistributionManagerIds();
    if (filter.getId() != null) {
      distributionManagerIds = distributionManagerIds.stream().filter(
              internalDistributedMember -> (filter.getId().equals(internalDistributedMember.getName())))
              .collect(Collectors.toSet());
    }

    return distributionManagerIds.stream()
            .map(DistributedMember.class::cast).collect(Collectors.toSet());
  }

  @VisibleForTesting
  List<GatewayReceiverMXBean> getGatewayReceiverMXBeans(
          DistributedSystemMXBean distributedSystemMXBean, List<DistributedMember> members) {
    List<GatewayReceiverMXBean> gatewayReceiverMXBeans = new ArrayList<>();
    for (DistributedMember member : members) {
      GatewayReceiverMXBean bean = getGatewayReceiverMXBean(distributedSystemMXBean, member);
      if (bean != null) {
        gatewayReceiverMXBeans.add(bean);
      }
    }

    return gatewayReceiverMXBeans;
   }

  @VisibleForTesting
  GatewayReceiverMXBean getGatewayReceiverMXBean(DistributedSystemMXBean distributedSystemMXBean,
                                                 DistributedMember member
      ) {
    ObjectName gatewayReceiverObjectName = MBeanJMXAdapter.getGatewayReceiverMBeanName(member);
    GatewayReceiverMXBean gatewayReceiverMXBean = null;
    if (gatewayReceiverObjectName != null) {
      gatewayReceiverMXBean =
          getSystemManagementService().getMBeanProxy(gatewayReceiverObjectName, GatewayReceiverMXBean.class);
    }

    return gatewayReceiverMXBean;
  }

  @VisibleForTesting
  SystemManagementService getSystemManagementService() {
    return (SystemManagementService) ManagementService.getExistingManagementService(cache);
  }

}

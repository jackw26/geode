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

package org.apache.geode.management.internal.rest.controllers;

import static org.apache.geode.management.internal.rest.controllers.AbstractManagementController.MANAGEMENT_API_VERSION;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.ldap.Control;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.apache.geode.management.api.ClusterManagementResult;
import org.apache.geode.management.api.RestApiCommand;

@Controller("cli")
@RequestMapping(MANAGEMENT_API_VERSION)
public class GenericCLIController extends AbstractManagementController{



  private static Map<String, String> commandToUrls = new HashMap<String, String>(){{
    put("list region", "/v2/regions");
    put("create region", "/v2/regions");
  }};

  @RequestMapping(method = RequestMethod.GET, value = "/cli" )
  public String get(@RequestParam String command, HttpServletRequest request) {

    String viewName = "forward:" + commandToUrls.get(command);
    return viewName;
  }

  @RequestMapping(method = RequestMethod.POST, value = "/cli" )
  public String post(@RequestParam String command, HttpServletRequest request) {

    String viewName = "forward:" + commandToUrls.get(command);
    return viewName;
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/cli" )
  public String delete(@RequestParam String command, HttpServletRequest request) {

    String viewName = "forward:" + commandToUrls.get(command);
    return viewName;
  }

}



package es.tid.fiware.iot.ac.rs;

/*
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Jersey Provider implementation for {@link Tenant} annotation.
 */
public class TenantProvider
        extends AbstractHttpContextInjectable<String>
        implements InjectableProvider<Tenant, Type> {

    private final String tenantHeader;

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantProvider.class);

    public TenantProvider(String tenantHeader) {
        this.tenantHeader = tenantHeader;
    }

    @Override
    public String getValue(HttpContext httpContext) {
        List<String> headers = httpContext.getRequest().getRequestHeader(tenantHeader);
        if (headers == null || headers.size() == 0) {
            LOGGER.error("Tenant Header {} not present", tenantHeader);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        } else if (headers.size() > 1) {
            LOGGER.error("Too many Tenant Headers {}:", tenantHeader, headers);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        } else {
            return headers.get(0);
        }
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable getInjectable(ComponentContext componentContext, Tenant tenant, Type type) {
        if (type.equals(String.class)) {
            return this;
        }
        return null;
    }
}

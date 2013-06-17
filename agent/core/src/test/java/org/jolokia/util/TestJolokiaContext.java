/*
 * Copyright 2009-2013 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jolokia.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.management.ObjectName;

import org.jolokia.config.*;
import org.jolokia.converter.Converters;
import org.jolokia.detector.ServerHandle;
import org.jolokia.restrictor.AllowAllRestrictor;
import org.jolokia.restrictor.Restrictor;
import org.jolokia.service.JolokiaContext;
import org.jolokia.service.JolokiaService;

/**
 * @author roland
 * @since 19.04.13
 */
public class TestJolokiaContext implements JolokiaContext {

    SortedSet<? extends JolokiaService> services;
    LogHandler logHandler;
    Restrictor restrictor;
    Configuration config;
    Converters converters;
    ServerHandle handle;

    public TestJolokiaContext() {
        this(null,null,null,null,null);
    }

    private TestJolokiaContext(Configuration pConfig,
                               Restrictor pRestrictor,
                               LogHandler pLogHandler,
                               ServerHandle pHandle,
                               SortedSet<JolokiaService> pServices) {
        this.config = pConfig != null ? pConfig : new StaticConfiguration();
        this.logHandler = pLogHandler != null ? pLogHandler : new StdoutLogHandler();
        this.restrictor = pRestrictor != null ? pRestrictor : new AllowAllRestrictor();
        this.services = pServices != null ? pServices : new TreeSet<JolokiaService>();
        try {
            if (pHandle != null) {
                handle = pHandle;
            } else {
                URL url = new URL("http://localhost/jolokia");
                handle = new ServerHandle("vendor","product","version",url,null);
                handle.setJolokiaId(UUID.randomUUID().toString());
            }
        } catch (MalformedURLException e) {}
//        this.requestDispatchManager = new RequestDispatchManager(
//                pDispatchers != null ? pDispatchers :
//                        Arrays.<RequestDispatcher>asList(new LocalRequestDispatcher(this)));
        converters = new Converters();
    }



    public Converters getConverters() {
        return converters;
    }

    public <T extends JolokiaService> SortedSet<T> getServices(Class<T> pType) {
        return (SortedSet<T>) services;
    }

    public ServerHandle getServerHandle() {
        return handle;
    }

    public String getConfig(ConfigKey pKey) {
        return config.getConfig(pKey);
    }

    public Set<ConfigKey> getConfigKeys() {
        return config.getConfigKeys();
    }

    public void debug(String message) {
        logHandler.debug(message);
    }

    public void info(String message) {
        logHandler.info(message);
    }

    public void error(String message, Throwable t) {
        logHandler.error(message,t);
    }

    public boolean isDebug() {
        return logHandler.isDebug();
    }

    public boolean isHttpMethodAllowed(HttpMethod pMethod) {
        return restrictor.isHttpMethodAllowed(pMethod);
    }

    public boolean isTypeAllowed(RequestType pType) {
        return restrictor.isTypeAllowed(pType);
    }

    public boolean isAttributeReadAllowed(ObjectName pName, String pAttribute) {
        return restrictor.isAttributeReadAllowed(pName, pAttribute);
    }

    public boolean isAttributeWriteAllowed(ObjectName pName, String pAttribute) {
        return restrictor.isAttributeWriteAllowed(pName, pAttribute);
    }

    public boolean isOperationAllowed(ObjectName pName, String pOperation) {
        return restrictor.isOperationAllowed(pName, pOperation);
    }

    public boolean isRemoteAccessAllowed(String... pHostOrAddress) {
        return restrictor.isRemoteAccessAllowed(pHostOrAddress);
    }

    public boolean isCorsAccessAllowed(String pOrigin) {
        return restrictor.isCorsAccessAllowed(pOrigin);
    }

    // ==================================================================

    public static class Builder {

        LogHandler logHandler;
        Restrictor restrictor;
        Configuration config;
        Converters converters;
        ServerHandle handle;
        SortedSet<JolokiaService> services;

        public Builder config(Configuration config) {
            this.config = config;
            return this;
        }

        public Builder config(Object... keyAndValues) {
            this.config = new StaticConfiguration(keyAndValues);
            return this;
        }

        public Builder restrictor(Restrictor pRestrictor) {
            this.restrictor = pRestrictor;
            return this;
        }

        public Builder serverHandle(ServerHandle pServerHandle) {
            this.handle = pServerHandle;
            return this;
        }

        public Builder logHandler(LogHandler pLogHandler) {
            logHandler = pLogHandler;
            return this;
        }

        public <T extends JolokiaService> Builder services(T ... pServices) {
            services = new TreeSet(Arrays.asList(pServices));
            return this;
        }
        public TestJolokiaContext build() {

            return new TestJolokiaContext(
                    config,
                    restrictor,
                    logHandler,
                    handle,
                    services
            );
        }

    }

}

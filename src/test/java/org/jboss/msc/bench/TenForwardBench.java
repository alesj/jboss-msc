/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.msc.bench;

import java.util.ArrayList;
import java.util.List;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.util.LatchedFinishListener;

public class TenForwardBench {

    public static void main(String[] args) throws Exception {
        final int totalServiceDefinitions = Integer.parseInt(args[0]);

        final ServiceContainer container = ServiceContainer.Factory.create();

        final LatchedFinishListener listener = new LatchedFinishListener();
        container.addListener(listener);

        for (int i = 0; i < totalServiceDefinitions; i++) {
            List<ServiceName> deps = new ArrayList<ServiceName>();
            int numDeps = Math.min(10, totalServiceDefinitions - i - 1);

            for (int j = 1; j < numDeps + 1; j++) {
                deps.add(ServiceName.of(("test" + (i + j)).intern()));
            }
            final ServiceBuilder<Void> builder = container.addService(ServiceName.of(("test" + i).intern()), Service.NULL);
            for (ServiceName dep : deps) {
                builder.addDependency(dep);
            }
        }

        listener.await();
        System.out.println(totalServiceDefinitions + " : "  + listener.getElapsedTime() / 1000.0);
        container.shutdown();
    }
}

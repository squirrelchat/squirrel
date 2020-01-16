/*
 * Copyright (c) 2020-present Bowser65 & vinceh121, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package chat.squirrel.core;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chat.squirrel.modules.AbstractModule;

public class ModuleManager {
    private static final Logger LOG = LoggerFactory.getLogger(ModuleManager.class);
    private static final Map<String, AbstractModule> modules = new HashMap<>();

    public void loadModules() {
        modules.forEach((c, m) -> {
            LOG.debug("Init module " + m.getClass().getCanonicalName());
            m.initialize();
            if (m.shouldEnable()) {
                m.enable();
            }
        });
    }

    public void disableModules() {
        LOG.info("Shutting down all modules");
        for (AbstractModule m : modules.values()) {
            m.disable();
        }
    }

    public void loadModule(String mod) {
        modules.get(mod).enable();
    }

    public void unloadModule(String mod) {
        modules.get(mod).disable();
    }

    public void scanPackage(String pkg) {
        final Reflections reflections = new Reflections(pkg, new SubTypesScanner());
        reflections.getSubTypesOf(AbstractModule.class).forEach(cls -> {
            if (!cls.isInterface() && !Modifier.isAbstract(cls.getModifiers())) {
                try {
                    modules.put(cls.getCanonicalName(),
                            (AbstractModule) cls.getDeclaredConstructors()[0].newInstance());
                } catch (Exception e) {
                    LOG.warn("Failed to load module \"" + cls.getSimpleName() + "\"", e);
                }
            }
        });
    }
}

/*
 * The MIT License
 *
 * Copyright 2012 Robert Sandell. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package jenkins.plugins.versionedbuildstep;

import hudson.Plugin;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import jenkins.model.Jenkins;
import jenkins.plugins.versionedbuildstep.model.AbstractRepository;
import jenkins.plugins.versionedbuildstep.timers.CentralRepositoriesUpdater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class PluginImpl extends Plugin {

    private static Logger logger = Logger.getLogger(PluginImpl.class.getName());

    /**
     * Master node needs to be started and there is no init milestone for slaves connected.
     * Lets hope 10 seconds is enough.
     */
    private static final long CENTRAL_REPOSITORIES_START = TimeUnit.SECONDS.toMillis(10);
    private static final long CENTRAL_REPOSITORIES_PERIOD = TimeUnit.MINUTES.toMillis(3);

    private Map<String, AbstractRepository> centralRepositories;
    private transient Timer timer;
    private transient CentralRepositoriesUpdater centralRepositoriesUpdater;

    @Override
    public void start() throws Exception {
        load();
        if (centralRepositories == null) {
            centralRepositories = new HashMap<String, AbstractRepository>();
        }
        timer = new Timer("Repositories Update Timer");
    }

    private void initializeUpdaters() {
        logger.info("Initializing Repository updaters...");
        centralRepositoriesUpdater = new CentralRepositoriesUpdater();
        timer.scheduleAtFixedRate(centralRepositoriesUpdater,
                CENTRAL_REPOSITORIES_START,
                CENTRAL_REPOSITORIES_PERIOD);
    }

    @Initializer(after = InitMilestone.JOB_LOADED, before = InitMilestone.COMPLETED)
    public static void init() throws IOException {
        getInstance().initializeUpdaters();
    }

    @Override
    public void stop() throws Exception {
        timer.cancel();
    }

    public static PluginImpl getInstance() {
        PluginImpl instance = Jenkins.getInstance().getPlugin(PluginImpl.class);
        if (instance == null) {
            throw new IllegalStateException("Plugin not loaded!");
        }
        return instance;
    }

    public Map<String, AbstractRepository> getCentralRepositories() {
        return centralRepositories;
    }
}

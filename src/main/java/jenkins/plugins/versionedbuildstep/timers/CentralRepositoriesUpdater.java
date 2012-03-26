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

package jenkins.plugins.versionedbuildstep.timers;

import jenkins.plugins.versionedbuildstep.PluginImpl;
import jenkins.plugins.versionedbuildstep.model.AbstractRepository;

import java.util.Collection;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main update worker for {@link jenkins.plugins.versionedbuildstep.CentralRepositories}.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class CentralRepositoriesUpdater extends TimerTask {

    private static Logger logger = Logger.getLogger(CentralRepositoriesUpdater.class.getName());

    @Override
    public void run() {
        logger.finer("Running...");
        Collection<AbstractRepository> repositories = PluginImpl.getInstance().getCentralRepositories().values();
        for (AbstractRepository repository : repositories) {
            try {
                repository.update();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error while updating " + repository.getName(), ex);
            }
        }
        logger.finer("Done...");
    }
}

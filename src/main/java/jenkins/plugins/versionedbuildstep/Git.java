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

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.Hudson;
import hudson.plugins.git.GitAPI;
import hudson.plugins.git.GitException;
import hudson.plugins.git.IGitAPI;
import hudson.util.LogTaskListener;
import hudson.util.StreamTaskListener;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Git Wrapper.
 * Using classes from the git-plugin to do most of the work.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class Git {

    private static final Logger logger = Logger.getLogger(Git.class.getName());

    IGitAPI api;
    private FilePath logFile;


    public Git(FilePath workspace) throws IOException, InterruptedException {
        logFile = workspace.getParent().child("repo-" + workspace.getName() + ".log");
        OutputStream logStream = logFile.write();
        EnvVars environment = Hudson.getInstance().toComputer().getEnvironment();
        api = new GitAPI("git", workspace, new StreamTaskListener(logStream),
                environment, null);
    }


    public void doClone(String url) throws GitException {
        RemoteConfig config = newRemoteConfig(url);
        api.clone(config);
    }

    public void doFetch(String url) throws GitException {
        api.fetch(newRemoteConfig(url));
    }

    public boolean isInitialized() {
        return api.hasGitRepo();
    }

    private RemoteConfig newRemoteConfig(String url) {
        return newRemoteConfig("origin", url, new RefSpec("*:*"));
    }

    /**
     * Code copied from the Git Plugin
     * ({@link hudson.plugins.git.GitSCM#newRemoteConfig(String, String, org.eclipse.jgit.transport.RefSpec)}).
     * @param name the name of the remote
     * @param refUrl the url
     * @param refSpec the ref spec.
     * @return a RemoteConfig saleable for clone and fetch.
     */
    private RemoteConfig newRemoteConfig(String name, String refUrl, RefSpec refSpec) {

        try {
            Config repoConfig = new Config();
            // Make up a repo config from the request parameters

            repoConfig.setString("remote", name, "url", refUrl);
            repoConfig.setString("remote", name, "fetch", refSpec.toString());

            return RemoteConfig.getAllRemoteConfigs(repoConfig).get(0);
        } catch (Exception ex) {
            throw new GitException("Error trying to create JGit configuration", ex);
        }
    }
}

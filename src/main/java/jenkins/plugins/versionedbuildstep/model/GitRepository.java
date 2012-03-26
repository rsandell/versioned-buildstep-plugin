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

package jenkins.plugins.versionedbuildstep.model;

import hudson.Extension;
import hudson.FilePath;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.plugins.git.GitException;
import jenkins.model.Jenkins;
import jenkins.plugins.versionedbuildstep.Git;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for repositories.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class GitRepository extends AbstractRepository {

    private static final Logger logger = Logger.getLogger(GitRepository.class.getName());

    private String url;
    private transient Git git;
    private transient String warning;
    private transient String error;

    private GitRepository(String name, FilePath repoDir, Date created, Date lastUpdated, RepoContainer<AbstractRepository> owner, String url) {
        super(name, repoDir, created, lastUpdated, owner);
        this.url = url;
    }

    public GitRepository(RepoContainer<AbstractRepository> container, FilePath baseDir, String name) throws IOException, InterruptedException {
        super(container, baseDir, name);
    }

    protected synchronized Git getGit() throws IOException, InterruptedException {
        if (git == null) {
            git = new Git(getRepoDir());
        }
        return git;
    }

    @Override
    public void init() {
        logger.log(Level.FINE, "Running Init {0}...", getName());
        warning = null;
        error = null;
        try {
            if (Util.fixEmpty(url) != null) {
                getGit().doClone(url);
            } else {
                warning = "URL is not set.";
            }
        } catch (GitException ge) {
            logger.log(Level.WARNING, "Exception during git init of " + getName(), ge);
            error = "git error during init: " + ge.getMessage();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Exception during git init of " + getName(), e);
            error = "I/O error during init: " + e.getMessage();
        } catch (InterruptedException e) {
            warning = "Interrupted while running init: " + e.getMessage();
        }
        logger.log(Level.FINE, "Init Done {0}", getName());
    }

    @Override
    public void update() {
        logger.log(Level.FINE, "Running Update {0}...", getName());
        warning = null;
        error = null;
        try {
            if (getGit().isInitialized()) {
                if (Util.fixEmpty(url) != null) {
                    getGit().doFetch(url);
                } else {
                    warning = "URL is not set.";
                }
            } else {
                init();
            }
        } catch (GitException ge) {
            logger.log(Level.WARNING, "Exception during git update of " + getName(), ge);
            error = "git error during update: " + ge.getMessage();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Exception during git update of " + getName(), e);
            error = "I/O error during update: " + e.getMessage();
        } catch (InterruptedException e) {
            warning = "Interrupted while running update: " + e.getMessage();
        }
        logger.log(Level.FINE, "Update Done {0}", getName());
    }

    @Override
    public Collection<String> getErrors() {
        if (Util.fixEmpty(error) != null) {
            return Collections.singleton(error);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<String> getWarnings() {
        if (Util.fixEmpty(warning) != null) {
            return Collections.singleton(warning);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void reConfigure(StaplerRequest request, StaplerResponse response) throws Descriptor.FormException, ServletException {
        url = request.getSubmittedForm().getString("url");
        git = null;
    }

    @Override
    public AbstractRepository copy(RepoContainer<AbstractRepository> owner, String newName) {
        GitRepository repository = new GitRepository(newName,
                getRepoDir().getParent().child(newName), new Date(), null, owner, url);

        return repository;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public Descriptor<AbstractRepository> getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(GitRepositoryDescriptor.class);
    }

    @Extension
    public static class GitRepositoryDescriptor extends RepositoryDescriptor {

        @Override
        public String getDisplayName() {
            return "Git Repository";
        }

        @Override
        public AbstractRepository createInstance(RepoContainer<AbstractRepository> container, FilePath baseDir, StaplerRequest req, String name) throws IOException, InterruptedException {
            return new GitRepository(container, baseDir, name);
        }
    }
}

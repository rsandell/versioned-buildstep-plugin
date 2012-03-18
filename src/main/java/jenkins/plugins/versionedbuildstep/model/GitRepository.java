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
import hudson.model.Descriptor;
import hudson.plugins.git.GitException;
import jenkins.model.Jenkins;
import jenkins.plugins.versionedbuildstep.CentralRepositories;
import jenkins.plugins.versionedbuildstep.Git;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;

/**
 * Base class for repositories.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class GitRepository extends AbstractRepository {

    private String url;

    private transient Git git;

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

    public void init() throws GitException, IOException, InterruptedException {
        getGit().doClone(url);
    }

    public void update() throws GitException, IOException, InterruptedException {
        if (getGit().isInitialized()) {
            getGit().doFetch(url);
        } else {
            init();
        }
    }

    @Override
    public void reConfigure(StaplerRequest request, StaplerResponse response) throws Descriptor.FormException, ServletException {
        url = request.getSubmittedForm().getString("url");
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

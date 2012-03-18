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

import hudson.ExtensionList;
import hudson.FilePath;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;
import jenkins.plugins.versionedbuildstep.CentralRepositories;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;

/**
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public abstract class AbstractRepository implements Describable<AbstractRepository> {

    private final String name;
    private final FilePath repoDir;
    private final Date created;
    private Date lastUpdated;
    protected RepoContainer<AbstractRepository> owner;

    protected AbstractRepository(RepoContainer<AbstractRepository> container, FilePath baseDir, String name)
            throws IOException, InterruptedException {
        Jenkins.checkGoodName(name);

        this.owner = container;
        this.name = name;
        repoDir = baseDir.child(name);
        repoDir.mkdirs();
        this.created = new Date();
    }

    protected AbstractRepository(String name, FilePath repoDir, Date created, Date lastUpdated, RepoContainer<AbstractRepository> owner) {
        this.name = name;
        this.repoDir = repoDir;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.owner = owner;
    }

    public abstract void init() throws IOException, InterruptedException;

    public abstract void update() throws IOException, InterruptedException;

    public abstract void reConfigure(StaplerRequest request, StaplerResponse response) throws Descriptor.FormException, ServletException;

    public void doConfigSubmit(StaplerRequest request, StaplerResponse response) throws Descriptor.FormException, ServletException {
        reConfigure(request, response);
        try {
            owner.save();
        } catch (IOException e) {
            throw new Descriptor.FormException("Unable to save: " + e.getMessage(), e, "name");
        }
    }

    public String getName() {
        return name;
    }

    public FilePath getRepoDir() {
        return repoDir;
    }

    public Date getCreated() {
        return created;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setOwner(RepoContainer<AbstractRepository> container) {
        this.owner = container;
    }

    public abstract AbstractRepository copy(RepoContainer<AbstractRepository> owner, String newName);

    public static abstract class RepositoryDescriptor extends Descriptor<AbstractRepository> {

        public abstract AbstractRepository createInstance(RepoContainer<AbstractRepository> container,
                                                          FilePath baseDir, StaplerRequest req, String name) throws IOException, InterruptedException;

        @Override
        public AbstractRepository newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            RepoContainer object = req.findAncestorObject(RepoContainer.class);
            if (object == null) {
                throw new FormException("The repository must be created in a valid container!", "name");
            }
            try {
                String name = formData.optString("name");
                if (name == null || name.isEmpty()) {
                    throw new FormException("Must provide a name!", "name");
                }
                return createInstance(object, object.getRootDir(), req, name);
            } catch (Exception e) {
                throw new FormException("Unable to create repository: " + e.getMessage(), e, "name");
            }
        }

        public static ExtensionList<RepositoryDescriptor> all() {
            return Jenkins.getInstance().getDescriptorList(AbstractRepository.class);
        }
    }
}

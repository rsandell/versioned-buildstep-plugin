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

import hudson.FilePath;
import hudson.model.Failure;
import hudson.plugins.git.GitException;
import jenkins.model.Jenkins;
import jenkins.plugins.versionedbuildstep.Git;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Base class for repositories.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public abstract class AbstractRepository {
    private String name;
    private String url;
    private FilePath gitDir;
    private transient Git git;

    public AbstractRepository(FilePath baseDir, String name, String url) {
        Jenkins.checkGoodName(name);
        if (url == null || url.isEmpty()) {
            throw new Failure("URL must not be empty");
        }
        this.name = name;
        this.url = url;
        gitDir = baseDir.child(name);
    }

    protected synchronized Git getGit() throws IOException, InterruptedException {
        if (git == null) {
            git = new Git(gitDir);
        }
        return git;
    }

    public void init() throws GitException, IOException, InterruptedException {
        getGit().doClone(url);
    }

    public void update() throws GitException, IOException, InterruptedException {
        getGit().doFetch(url);
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FilePath getGitDir() {
        return gitDir;
    }

}

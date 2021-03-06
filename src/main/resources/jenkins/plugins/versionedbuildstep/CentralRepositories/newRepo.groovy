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

package jenkins.plugins.versionedbuildstep.CentralRepositories

import jenkins.plugins.versionedbuildstep.CentralRepositories
import jenkins.plugins.versionedbuildstep.model.AbstractRepository

/**
 * 
 *
 *@author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
def f = namespace(lib.FormTagLib)
def l = namespace(lib.LayoutTagLib)
def j = namespace(lib.JenkinsTagLib)
def n = namespace(lib.jenkins.NewFromListTagLib)


l.layout(title: _("Central Build script Repositories")) {
    l.side_panel() {

    }
    def instance = CentralRepositories.getInstance();
    def repoDescriptors = AbstractRepository.RepositoryDescriptor.all();
    //def descriptor = it.descriptor;

    l.main_panel() {
        n.form(nameTitle: _("New Repo"), action: "newRepoSubmit",
        descriptors: repoDescriptors, checkUrl: "checkNewRepoName",
                copyNames: instance.getRepoNames(), copyTitle: _("Copy Existing"))
    }
}
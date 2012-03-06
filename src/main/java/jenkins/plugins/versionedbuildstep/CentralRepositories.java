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

import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.ManagementLink;

/**
 * Management page for the central repositories that all users can use.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Extension
public class CentralRepositories extends ManagementLink {
    @Override
    public String getIconFileName() {
        return "package.png";
    }

    @Override
    public String getDisplayName() {
        return "Build script repositories";
    }

    @Override
    public String getUrlName() {
        return "build-script-repositories";
    }

    public CentralRepositories getInstance() {
        for (ManagementLink l : Hudson.getInstance().getManagementLinks()) {
            if (l instanceof CentralRepositories) {
                return (CentralRepositories)l;
            }
        }
        throw new IllegalStateException("The CentralRepositories ManagementLink is not loaded yet!");
    }
}

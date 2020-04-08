/*
 * Copyright (c) 2020 Squirrel Chat, All rights reserved.
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

package chat.squirrel.upload;

import org.bson.types.ObjectId;

import java.io.InputStream;

public class UploadAction {
    private InputStream input;
    private String filename;
    private Bucket bucket;
    private ObjectId resourceId;

    private int maxFileSize = 0;
    private FileType[] allowedTypes = null;

    private boolean allowAnimated = false;
    private int resizeWidth = 0;
    private int[] ratio = null;

    public InputStream getInput() {
        return input;
    }

    public void setInput(final InputStream input) {
        this.input = input;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(final Bucket bucket) {
        this.bucket = bucket;
    }

    public ObjectId getResourceId() {
        return resourceId;
    }

    public void setResourceId(final ObjectId resourceId) {
        this.resourceId = resourceId;
    }

    public int getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(final int maxFileSize) {
        if (maxFileSize < 0) throw new IllegalArgumentException();
        this.maxFileSize = maxFileSize;
    }

    public FileType[] getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(final FileType... allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public boolean isAllowAnimated() {
        return allowAnimated;
    }

    public void setAllowAnimated(final boolean allowAnimated) {
        this.allowAnimated = allowAnimated;
    }

    public int getResizeWidth() {
        return resizeWidth;
    }

    public void setResizeWidth(final int resizeWidth) {
        if (resizeWidth < 0) throw new IllegalArgumentException();
        this.resizeWidth = resizeWidth;
    }

    public int[] getRatio() {
        return ratio;
    }

    public void setRatio(final int w, final int h) {
        if (w < 0 || h < 0) throw new IllegalArgumentException();
        this.ratio = new int[]{w, h};
    }

    public boolean isUsable() {
        return input != null && bucket != null && resourceId != null &&
                (bucket != Bucket.ATTACHMENT || filename != null);
    }
}

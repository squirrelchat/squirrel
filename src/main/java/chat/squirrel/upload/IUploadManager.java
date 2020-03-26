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

public interface IUploadManager {
    /**
     * Handles an upload, validates its contents and saves it on disk.
     *
     * @param action Details of the action
     * @return The result of the transaction.
     */
    ActionResult upload(UploadAction action);

    /**
     * Retrieves an uploaded file from storage.
     *
     * @param bucket     Bucket where the upload is saved.
     * @param resourceId ID of the resource.
     * @param assetId    ID of the asset.
     * @param animated   Whether we want the animated version of the asset.
     * @return An {@code InputStream}, or null if not found.
     */
    InputStream retrieve(Bucket bucket, ObjectId resourceId, String assetId, boolean animated);

    InputStream retrieveNamed(Bucket bucket, ObjectId resourceId, String assetId, String filename);

    boolean delete(Bucket bucket, ObjectId resourceId, String assetId);

    boolean deleteResource(Bucket bucket, ObjectId resourceId);
}

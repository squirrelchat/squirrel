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

package chat.squirrel.upload.managers;

import chat.squirrel.Squirrel;
import chat.squirrel.config.TableUserConfig;
import chat.squirrel.upload.ActionResult;
import chat.squirrel.upload.Bucket;
import chat.squirrel.upload.IUploadManager;
import chat.squirrel.upload.UploadAction;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;

public class LocalUploadManager implements IUploadManager {
    private static final Logger LOG = LoggerFactory.getLogger(LocalUploadManager.class);
    private final File uploadTarget;

    public LocalUploadManager() {
        this(getDefaultUploadFolder());
    }

    public LocalUploadManager(@Nonnull final File uploadTarget) {
        if (uploadTarget.isDirectory()) {
            throw new IllegalArgumentException("Upload target is not a directory");
        }

        this.uploadTarget = uploadTarget;
    }

    @Override
    public ActionResult upload(final UploadAction action) {
        final ActionResult res = new ActionResult();
        if (!action.isUsable()) {
            LOG.error("Received an invalid upload action! This is likely due to an internal error.");
            res.setErrorReason(ActionResult.ErrorReason.INTERNAL_ERROR);
            return res;
        }

        try {
            if (action.getMaxFileSize() > 0 && action.getInput().available() > action.getMaxFileSize()) {
                res.setErrorReason(ActionResult.ErrorReason.FILE_TOO_LARGE);
                return res;
            }
        } catch (final IOException e) {
            LOG.error("Failed to validate file size", e);
            res.setErrorReason(ActionResult.ErrorReason.INTERNAL_ERROR);
            return res;
        }

        if (action.getAllowedTypes() != null && action.getAllowedTypes().length != 0 && false) { // TODO
            res.setErrorReason(ActionResult.ErrorReason.INVALID_TYPE);
            return res;
        }

        File folder = this.getBaseFolderFor(action.getBucket(), action.getResourceId());
        if (action.getBucket().isKeepFilename()) {
            // If we keep the filename, we assume there is no processing or validation involved.
            final String id = (action.isAllowAnimated() ? "a_" : "") + ObjectId.get().toHexString();
            res.setAssetId(id);
            folder = new File(folder, id);
            mkdir(folder);

            final File outFile = new File(folder, action.getFilename());
            if (!saveFile(action.getInput(), outFile)) {
                res.setErrorReason(ActionResult.ErrorReason.INTERNAL_ERROR);
                return res;
            }
        } else {
            final boolean isAnimated = false; // TODO: Check APNG/GIF
            final String id = (isAnimated ? "a_" : "") + ObjectId.get().toHexString();
            res.setAssetId(id);

            BufferedImage image;
            try {
                image = ImageIO.read(action.getInput());
            } catch (IOException e) {
                LOG.error("Failed to read image", e);
                res.setErrorReason(ActionResult.ErrorReason.CORRUPTED_FILE);
                return res;
            }

            if (action.getRatio() != null) {
                final int width = image.getWidth();
                final int height = image.getWidth();
                final float ratioW = action.getRatio()[0];
                final float ratioH = action.getRatio()[1];
                if ((((width * ratioH) / ratioW) - height) == 0) {
                    res.setErrorReason(ActionResult.ErrorReason.IMAGE_INVALID_RATIO);
                    return res;
                }
            }

            if (action.getResizeWidth() != 0) {
                // TODO: Resize
            }

            try {
                ImageIO.write(image, "png", new File(folder, id + ".png"));
            } catch (IOException e) {
                LOG.error("Failed to save image", e);
                res.setErrorReason(ActionResult.ErrorReason.INTERNAL_ERROR);
                return res;
            }

            if (isAnimated) {
                // TODO: APNG/GIF processing
            }
        }

        res.setSuccessful(true);
        return res;
    }

    @Override
    public InputStream retrieve(final Bucket bucket, final ObjectId resourceId, final String assetId, final boolean animated) {
        if (animated && !assetId.startsWith("a_")) return null;
        return retrieve(new File(this.getBaseFolderFor(bucket, resourceId), assetId + '.' + (animated ? "gif" : "png")));
    }

    @Override
    public InputStream retrieveNamed(final Bucket bucket, final ObjectId resourceId, final String assetId, final String filename) {
        return retrieve(Paths.get(this.getBaseFolderFor(bucket, resourceId).getAbsolutePath(), assetId, filename).toFile());
    }

    private InputStream retrieve(final File file) {
        if (!file.exists()) return null;
        if (!file.isFile()) {
            // TODO: Should we attempt to fix the storage and delete this?
            LOG.warn("Encountered an unexpected directory; Expected " + file.getAbsolutePath() + " to be a file.");
            return null;
        }
        FileInputStream input;
        try {
            input = new FileInputStream(file);
        } catch (final FileNotFoundException e) {
            // Should never be reached
            return null;
        }
        return input;
    }

    @Override
    public boolean delete(final Bucket bucket, final ObjectId resourceId, final String assetId) {
        if (bucket.isKeepFilename()) {
            return delete(Paths.get(this.getBaseFolderFor(bucket, resourceId).getAbsolutePath(), assetId).toFile());
        }

        final File png = Paths.get(this.getBaseFolderFor(bucket, resourceId).getAbsolutePath(), assetId + ".png").toFile();
        final File gif = Paths.get(this.getBaseFolderFor(bucket, resourceId).getAbsolutePath(), assetId + ".gif").toFile();
        if (png.exists()) return png.delete();
        if (gif.exists()) return gif.delete();
        return true;
    }

    @Override
    public boolean deleteResource(final Bucket bucket, final ObjectId resourceId) {
        return delete(this.getBaseFolderFor(bucket, resourceId));
    }

    private boolean saveFile(final InputStream input, final File target) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(target);
        } catch (final FileNotFoundException e) {
            LOG.error("Failed to open output stream", e);
            return false;
        }

        try {
            input.transferTo(out);
        } catch (final IOException e) {
            LOG.error("Failed to write to output stream", e);
            return false;
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                LOG.error("Failed to close output stream", e);
                // noinspection ReturnInsideFinallyBlock
                return false;
            }
        }
        return true;
    }

    private void mkdir(final File file) {
        if (file.isFile()) {
            LOG.warn(file.getAbsolutePath() + " is supposed to be a folder but found a file instead. Deleting.");
            if (!file.delete()) {
                throw new IllegalStateException("Failed to delete file " + file.getAbsolutePath());
            }
        }
        if (!file.exists() && !file.mkdir()) {
            throw new IllegalStateException("Failed to create bucket folder at " + file.getAbsolutePath());
        }
    }

    private File getBaseFolderFor(final Bucket bucket, final ObjectId resourceId) {
        final File bucketFolder = new File(this.uploadTarget, bucket.toString().toLowerCase());
        final File baseFolder = new File(bucketFolder, resourceId.toHexString());
        mkdir(bucketFolder);
        mkdir(baseFolder);
        return baseFolder;
    }

    private boolean delete(final File file) {
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!delete(f)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    private static File getDefaultUploadFolder() {
        // TODO: no
        final TableUserConfig conf = (TableUserConfig) Squirrel.getInstance()
                .getUserConfig(LocalUploadManager.class, new TableUserConfig(LocalUploadManager.class));
        final Object rawUp = conf.getTable().get("upload_folder");
        if (!(rawUp instanceof String)) {
            throw new IllegalStateException("upload folder not set as string in config");
        }

        final String uploadTarget = (String) rawUp;

        return new File(uploadTarget);
    }
}

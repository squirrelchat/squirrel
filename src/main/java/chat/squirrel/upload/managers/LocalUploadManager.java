package chat.squirrel.upload.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;

import chat.squirrel.Squirrel;
import chat.squirrel.config.TableUserConfig;
import chat.squirrel.upload.AbstractUploadManager;
import chat.squirrel.upload.ActionResult;
import chat.squirrel.upload.Asset;
import chat.squirrel.upload.Bucket;

public class LocalUploadManager extends AbstractUploadManager {
    private final File uploadFolder;

    public LocalUploadManager() {
        this(getDefaultUploadFolder());
    }

    public LocalUploadManager(@Nonnull final File uploadFolder) {
        if (uploadFolder.isDirectory()) {
            throw new IllegalArgumentException("upload folder does not represent a directory");
        }

        this.uploadFolder = uploadFolder;
    }

    @Override
    public ActionResult upload(final Bucket bucket, final String type, final InputStream input) {
        final ActionResult res = new ActionResult();

        final ObjectId id = new ObjectId();
        res.setAssetId(id.toHexString());

        final File bucketFile = this.getBucketFolder(bucket);

        final File outFile = new File(bucketFile, id.toHexString());

        FileOutputStream out;
        try {
            out = new FileOutputStream(outFile);
        } catch (final FileNotFoundException e) {
            res.setErrorReason("Failed to open output stream: " + e.toString());
            return res;
        }

        try {
            final MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-1");
            } catch (final NoSuchAlgorithmException e1) {
                throw new IllegalStateException("Should not be reached");
            }

            final byte[] buffer = new byte[4096];
            try {
                while (input.read(buffer) >= 0) {
                    out.write(buffer);
                    md.update(buffer);
                }
            } catch (final IOException e1) {
                res.setErrorReason("Could not write to output stream or digest: " + e1.toString());
                return res;
            }

            final BigInteger num = new BigInteger(1, md.digest());
            final String hashtext = num.toString(16);
            res.setAssetHash(hashtext);
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                res.setErrorReason("Failed to close output stream: " + e.toString());
                return res;
            }
        }

        res.setSuccess(true);

        final Asset asset = new Asset(res.getAssetId(), res.getAssetHash(), res.getAssetType());
        this.insertAsset(asset);

        return res;
    }

    @Override
    public Asset retrieve(final Bucket bucket, final String id, final String hash, final String type) {
        final Asset asset = this.retrieveAsset(id);

        if (asset == null) {
            return null;
        }

        if (!asset.getHash().equals(hash)) { // in case the request hash is not correct. FIXME: might want to return
                                             // something else
            return null;
        }

        final File bucketFolder = this.getBucketFolder(bucket);

        final File target = new File(bucketFolder, id);

        if (!target.exists() || target.isDirectory()) {
            return null;
        }

        FileInputStream input;
        try {
            input = new FileInputStream(target);
        } catch (final FileNotFoundException e) {
            return null;
        }

        asset.setInput(input);

        return asset;
    }

    @Override
    public ActionResult delete(final Bucket bucket, final String id, final String hash) {
        final ActionResult res = new ActionResult();

        final Asset asset = this.retrieveAsset(id);

        if (asset == null) {
            res.setSuccess(false);
            return res;
        }

        final File bucketFolder = this.getBucketFolder(bucket);

        final File target = new File(bucketFolder, id);

        final boolean success = target.delete();

        res.setSuccess(success);

        if (success) {
            this.removeAsset(id);
        }

        return res;
    }

    public File getUploadFolder() {
        return this.uploadFolder;
    }

    public File getBucketFolder(final Bucket bucket) {
        final File f = new File(this.uploadFolder, bucket.toString().toLowerCase());
        if (f.isDirectory()) {
            throw new IllegalStateException(
                    "directory " + f.getAbsolutePath() + " is supposed to be a folder but found file");
        }
        if (!f.exists()) {
            if (!f.mkdir()) {
                throw new IllegalStateException("Failed to create bucket folder at " + f.getAbsolutePath());
            }
        }
        return f;
    }

    private static File getDefaultUploadFolder() {
        final TableUserConfig conf = (TableUserConfig) Squirrel.getInstance().getUserConfig(LocalUploadManager.class,
                new TableUserConfig(LocalUploadManager.class));
        final Object rawUp = conf.getTable().get("upload_folder");
        if (!(rawUp instanceof String)) {
            throw new IllegalStateException("upload folder not set as string in config");
        }

        final String uploadFolder = (String) rawUp;

        return new File(uploadFolder);
    }
}

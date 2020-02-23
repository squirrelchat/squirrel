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

import chat.squirrel.upload.AbstractUploadManager;
import chat.squirrel.upload.ActionResult;
import chat.squirrel.upload.Asset;
import chat.squirrel.upload.Bucket;

public class LocalUploadManager extends AbstractUploadManager {
    private final File uploadFolder;

    public LocalUploadManager(@Nonnull final File uploadFolder) {
        if (uploadFolder.isDirectory())
            throw new IllegalArgumentException("upload folder does not represent a directory");

        this.uploadFolder = uploadFolder;
    }

    @Override
    public ActionResult upload(Bucket bucket, String type, InputStream input) {
        final ActionResult res = new ActionResult();

        final ObjectId id = new ObjectId();
        res.setAssetId(id.toHexString());

        final File bucketFile = getBucketFolder(bucket);

        final File outFile = new File(bucketFile, id.toHexString());

        FileOutputStream out;
        try {
            out = new FileOutputStream(outFile);
        } catch (FileNotFoundException e) {
            res.setErrorReason("Failed to open output stream: " + e.toString());
            return res;
        }

        try {
            final MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e1) {
                throw new IllegalStateException("Should not be reached");
            }

            final byte[] buffer = new byte[4096];
            try {
                while (input.read(buffer) >= 0) {
                    out.write(buffer);
                    md.update(buffer);
                }
            } catch (IOException e1) {
                res.setErrorReason("Could not write to output stream or digest: " + e1.toString());
                return res;
            }

            final BigInteger num = new BigInteger(1, md.digest());
            final String hashtext = num.toString(16);
            res.setAssetHash(hashtext);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                res.setErrorReason("Failed to close output stream: " + e.toString());
                return res;
            }
        }

        res.setSuccess(true);

        final Asset asset = new Asset(res.getAssetId(), res.getAssetHash(), res.getAssetType());
        insertAsset(asset);

        return res;
    }

    @Override
    public Asset retrieve(Bucket bucket, String id, String hash, String type) {
        final Asset asset = retrieveAsset(id);

        if (asset == null) {
            return null;
        }

        if (!asset.getHash().equals(hash)) { // in case the request hash is not correct. FIXME: might want to return
                                             // something else
            return null;
        }

        final File bucketFolder = getBucketFolder(bucket);

        final File target = new File(bucketFolder, id);

        if (!target.exists() || target.isDirectory()) {
            return null;
        }

        FileInputStream input;
        try {
            input = new FileInputStream(target);
        } catch (FileNotFoundException e) {
            return null;
        }
        
        asset.setInput(input);
        
        return asset;
    }

    @Override
    public ActionResult delete(Bucket bucket, String id, String hash) {
        final ActionResult res = new ActionResult();
        
        final Asset asset = retrieveAsset(id);
        
        if (asset == null) {
            res.setSuccess(false);
            return res;
        }
        
        final File bucketFolder = getBucketFolder(bucket);

        final File target = new File(bucketFolder, id);
        
        final boolean success = target.delete();
        
        res.setSuccess(success);
        
        if (success) {
            removeAsset(id);
        }

        return res;
    }

    public File getUploadFolder() {
        return uploadFolder;
    }

    public File getBucketFolder(Bucket bucket) {
        final File f = new File(uploadFolder, bucket.toString().toLowerCase());
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

}

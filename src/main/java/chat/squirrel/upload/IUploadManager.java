package chat.squirrel.upload;

import java.io.InputStream;

import javax.annotation.Nullable;

public interface IUploadManager {
    ActionResult upload(Bucket bucket, String type, InputStream input);

    @Nullable
    Asset retrieve(Bucket bucket, String id, String hash, String type);
    
    ActionResult delete(Bucket bucket, String id, String hash);
}

package chat.squirrel.upload;

import java.io.InputStream;

import javax.annotation.Nullable;

public interface IUploadManager {
    ActionResult upload(Bucket bucket, String type, InputStream input);

    @Nullable
    InputStream retrieve(Asset asset);

    ActionResult delete(Asset asset);
}

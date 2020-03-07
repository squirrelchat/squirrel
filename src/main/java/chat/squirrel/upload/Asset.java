package chat.squirrel.upload;

import chat.squirrel.entities.AbstractEntity;

public class Asset extends AbstractEntity {
    private final String id, hash, type;
    private final Bucket bucket;

    public Asset(final String id, final String hash, final String type, final Bucket bucket) {
        this.id = id;
        this.hash = hash;
        this.type = type;
        this.bucket = bucket;
    }

    public String getAssetId() {
        return this.id;
    }

    public String getHash() {
        return this.hash;
    }

    public String getType() {
        return this.type;
    }

    public Bucket getBucket() {
        return bucket;
    }

}

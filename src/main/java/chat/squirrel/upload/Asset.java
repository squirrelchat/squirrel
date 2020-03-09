package chat.squirrel.upload;

import chat.squirrel.entities.AbstractEntity;

public class Asset extends AbstractEntity {
    private String assetId, hash, type;
    private Bucket bucket;

    public Asset(final String assetId, final String hash, final String type, final Bucket bucket) {
        this.assetId = assetId;
        this.hash = hash;
        this.type = type;
        this.bucket = bucket;
    }

    public String getAssetId() {
        return this.assetId;
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

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

}

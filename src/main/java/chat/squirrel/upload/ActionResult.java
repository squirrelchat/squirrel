package chat.squirrel.upload;

public class ActionResult {
    private boolean success = false;
    private String errorReason, assetId, assetHash, assetType;

    public String getAssetId() {
        return this.assetId;
    }

    public void setAssetId(final String assetId) {
        this.assetId = assetId;
    }

    public String getAssetHash() {
        return this.assetHash;
    }

    public void setAssetHash(final String assetHash) {
        this.assetHash = assetHash;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public String getErrorReason() {
        return this.errorReason;
    }

    public void setErrorReason(final String errorReason) {
        this.errorReason = errorReason;
    }

    public String getAssetType() {
        return this.assetType;
    }

    public void setAssetType(final String assetType) {
        this.assetType = assetType;
    }

}

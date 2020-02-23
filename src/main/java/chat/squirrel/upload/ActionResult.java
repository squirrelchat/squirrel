package chat.squirrel.upload;

public class ActionResult {
    private boolean success = false;
    private String errorReason, assetId, assetHash, assetType;

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetHash() {
        return assetHash;
    }

    public void setAssetHash(String assetHash) {
        this.assetHash = assetHash;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

}

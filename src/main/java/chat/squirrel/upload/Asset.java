package chat.squirrel.upload;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import chat.squirrel.entities.AbstractEntity;

public class Asset extends AbstractEntity implements Closeable {
    private final String id, hash, type;
    @BsonIgnore
    private InputStream input;

    public Asset(String id, String hash, String type, InputStream input) {
        this.id = id;
        this.hash = hash;
        this.type = type;
        this.input = input;
    }

    public Asset(String id, String hash, String type) {
        this(id, hash, type, (InputStream) null);
    }

    /**
     * Highly discouraged but here for testing
     * 
     * @param id
     * @param hash
     * @param type
     * @param data
     */
    public Asset(String id, String hash, String type, byte[] data) {
        this(id, hash, type, (InputStream) new ByteArrayInputStream(data));
    }

    public String getAssetId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public String getType() {
        return type;
    }

    @BsonIgnore
    public InputStream getInput() {
        return input;
    }

    @BsonIgnore
    public void setInput(InputStream input) {
        this.input = input;
    }

    @Override
    public void close() throws IOException {
        getInput().close();
    }

}

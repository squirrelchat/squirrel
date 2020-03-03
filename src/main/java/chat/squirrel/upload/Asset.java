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

    public Asset(final String id, final String hash, final String type, final InputStream input) {
        this.id = id;
        this.hash = hash;
        this.type = type;
        this.input = input;
    }

    public Asset(final String id, final String hash, final String type) {
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
    public Asset(final String id, final String hash, final String type, final byte[] data) {
        this(id, hash, type, new ByteArrayInputStream(data));
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

    @BsonIgnore
    public InputStream getInput() {
        return this.input;
    }

    @BsonIgnore
    public void setInput(final InputStream input) {
        this.input = input;
    }

    @Override
    public void close() throws IOException {
        this.getInput().close();
    }

}

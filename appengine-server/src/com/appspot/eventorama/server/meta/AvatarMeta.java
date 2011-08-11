package com.appspot.eventorama.server.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2011-08-11 13:53:23")
/** */
public final class AvatarMeta extends org.slim3.datastore.ModelMeta<com.appspot.eventorama.shared.model.Avatar> {

    /** */
    public final org.slim3.datastore.CoreUnindexedAttributeMeta<com.appspot.eventorama.shared.model.Avatar, byte[]> bytes = new org.slim3.datastore.CoreUnindexedAttributeMeta<com.appspot.eventorama.shared.model.Avatar, byte[]>(this, "bytes", "bytes", byte[].class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Avatar, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Avatar, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.Avatar> mimeType = new org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.Avatar>(this, "mimeType", "mimeType");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.appspot.eventorama.shared.model.Avatar, org.slim3.datastore.ModelRef<com.appspot.eventorama.shared.model.User>, com.appspot.eventorama.shared.model.User> userRef = new org.slim3.datastore.ModelRefAttributeMeta<com.appspot.eventorama.shared.model.Avatar, org.slim3.datastore.ModelRef<com.appspot.eventorama.shared.model.User>, com.appspot.eventorama.shared.model.User>(this, "userRef", "userRef", org.slim3.datastore.ModelRef.class, com.appspot.eventorama.shared.model.User.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Avatar, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Avatar, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final AvatarMeta slim3_singleton = new AvatarMeta();

    /**
     * @return the singleton
     */
    public static AvatarMeta get() {
       return slim3_singleton;
    }

    /** */
    public AvatarMeta() {
        super("Avatar", com.appspot.eventorama.shared.model.Avatar.class);
    }

    @Override
    public com.appspot.eventorama.shared.model.Avatar entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.appspot.eventorama.shared.model.Avatar model = new com.appspot.eventorama.shared.model.Avatar();
        model.setBytes(blobToBytes((com.google.appengine.api.datastore.Blob) entity.getProperty("bytes")));
        model.setKey(entity.getKey());
        model.setMimeType((java.lang.String) entity.getProperty("mimeType"));
        if (model.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) is null.");
        }
        model.getUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("userRef"));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.appspot.eventorama.shared.model.Avatar m = (com.appspot.eventorama.shared.model.Avatar) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setUnindexedProperty("bytes", bytesToBlob(m.getBytes()));
        entity.setProperty("mimeType", m.getMimeType());
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        entity.setProperty("userRef", m.getUserRef().getKey());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.appspot.eventorama.shared.model.Avatar m = (com.appspot.eventorama.shared.model.Avatar) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.appspot.eventorama.shared.model.Avatar m = (com.appspot.eventorama.shared.model.Avatar) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.appspot.eventorama.shared.model.Avatar m = (com.appspot.eventorama.shared.model.Avatar) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.appspot.eventorama.shared.model.Avatar m = (com.appspot.eventorama.shared.model.Avatar) model;
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        m.getUserRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
        com.appspot.eventorama.shared.model.Avatar m = (com.appspot.eventorama.shared.model.Avatar) model;
        long version = m.getVersion() != null ? m.getVersion().longValue() : 0L;
        m.setVersion(Long.valueOf(version + 1L));
    }

    @Override
    protected void prePut(Object model) {
    }

    @Override
    public String getSchemaVersionName() {
        return "slim3.schemaVersion";
    }

    @Override
    public String getClassHierarchyListName() {
        return "slim3.classHierarchyList";
    }

    @Override
    protected boolean isCipherProperty(String propertyName) {
        return false;
    }

    @Override
    protected void modelToJson(org.slim3.datastore.json.JsonWriter writer, java.lang.Object model, int maxDepth, int currentDepth) {
        com.appspot.eventorama.shared.model.Avatar m = (com.appspot.eventorama.shared.model.Avatar) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getBytes() != null){
            writer.setNextPropertyName("bytes");
            encoder0.encode(writer, new com.google.appengine.api.datastore.ShortBlob(m.getBytes()));
        }
        if(m.getKey() != null){
            writer.setNextPropertyName("key");
            encoder0.encode(writer, m.getKey());
        }
        if(m.getMimeType() != null){
            writer.setNextPropertyName("mimeType");
            encoder0.encode(writer, m.getMimeType());
        }
        if(m.getUserRef() != null && m.getUserRef().getKey() != null){
            writer.setNextPropertyName("userRef");
            encoder0.encode(writer, m.getUserRef(), maxDepth, currentDepth);
        }
        if(m.getVersion() != null){
            writer.setNextPropertyName("version");
            encoder0.encode(writer, m.getVersion());
        }
        writer.endObject();
    }

    @Override
    protected com.appspot.eventorama.shared.model.Avatar jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.appspot.eventorama.shared.model.Avatar m = new com.appspot.eventorama.shared.model.Avatar();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("bytes");
        if(m.getBytes() != null){
            m.setBytes(decoder0.decode(reader, new com.google.appengine.api.datastore.ShortBlob(m.getBytes())).getBytes());
        } else{
            com.google.appengine.api.datastore.ShortBlob v = decoder0.decode(reader, (com.google.appengine.api.datastore.ShortBlob)null);
            if(v != null){
                m.setBytes(v.getBytes());
            } else{
                m.setBytes(null);
            }
        }
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("mimeType");
        m.setMimeType(decoder0.decode(reader, m.getMimeType()));
        reader = rootReader.newObjectReader("userRef");
        decoder0.decode(reader, m.getUserRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("version");
        m.setVersion(decoder0.decode(reader, m.getVersion()));
        return m;
    }
}
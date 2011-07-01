package com.appspot.eventorama.server.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2011-07-01 15:27:50")
/** */
public final class ActivityMeta extends org.slim3.datastore.ModelMeta<com.appspot.eventorama.shared.model.Activity> {

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.appspot.eventorama.shared.model.Activity, org.slim3.datastore.ModelRef<com.appspot.eventorama.shared.model.Application>, com.appspot.eventorama.shared.model.Application> applicationRef = new org.slim3.datastore.ModelRefAttributeMeta<com.appspot.eventorama.shared.model.Activity, org.slim3.datastore.ModelRef<com.appspot.eventorama.shared.model.Application>, com.appspot.eventorama.shared.model.Application>(this, "applicationRef", "applicationRef", org.slim3.datastore.ModelRef.class, com.appspot.eventorama.shared.model.Application.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Activity, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Activity, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Activity, com.google.appengine.api.datastore.Link> photoUrl = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Activity, com.google.appengine.api.datastore.Link>(this, "photoUrl", "photoUrl", com.google.appengine.api.datastore.Link.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.Activity> text = new org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.Activity>(this, "text", "text");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Activity, java.util.Date> timestamp = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Activity, java.util.Date>(this, "timestamp", "timestamp", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Activity, java.lang.Integer> type = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Activity, java.lang.Integer>(this, "type", "type", int.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.appspot.eventorama.shared.model.Activity, org.slim3.datastore.ModelRef<com.appspot.eventorama.shared.model.User>, com.appspot.eventorama.shared.model.User> userRef = new org.slim3.datastore.ModelRefAttributeMeta<com.appspot.eventorama.shared.model.Activity, org.slim3.datastore.ModelRef<com.appspot.eventorama.shared.model.User>, com.appspot.eventorama.shared.model.User>(this, "userRef", "userRef", org.slim3.datastore.ModelRef.class, com.appspot.eventorama.shared.model.User.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Activity, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Activity, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final ActivityMeta slim3_singleton = new ActivityMeta();

    /**
     * @return the singleton
     */
    public static ActivityMeta get() {
       return slim3_singleton;
    }

    /** */
    public ActivityMeta() {
        super("Activity", com.appspot.eventorama.shared.model.Activity.class);
    }

    @Override
    public com.appspot.eventorama.shared.model.Activity entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.appspot.eventorama.shared.model.Activity model = new com.appspot.eventorama.shared.model.Activity();
        if (model.getApplicationRef() == null) {
            throw new NullPointerException("The property(applicationRef) is null.");
        }
        model.getApplicationRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("applicationRef"));
        model.setKey(entity.getKey());
        model.setPhotoUrl((com.google.appengine.api.datastore.Link) entity.getProperty("photoUrl"));
        model.setText((java.lang.String) entity.getProperty("text"));
        model.setTimestamp((java.util.Date) entity.getProperty("timestamp"));
        model.setType(longToPrimitiveInt((java.lang.Long) entity.getProperty("type")));
        if (model.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) is null.");
        }
        model.getUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("userRef"));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.appspot.eventorama.shared.model.Activity m = (com.appspot.eventorama.shared.model.Activity) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        if (m.getApplicationRef() == null) {
            throw new NullPointerException("The property(applicationRef) must not be null.");
        }
        entity.setProperty("applicationRef", m.getApplicationRef().getKey());
        entity.setProperty("photoUrl", m.getPhotoUrl());
        entity.setProperty("text", m.getText());
        entity.setProperty("timestamp", m.getTimestamp());
        entity.setProperty("type", m.getType());
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
        com.appspot.eventorama.shared.model.Activity m = (com.appspot.eventorama.shared.model.Activity) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.appspot.eventorama.shared.model.Activity m = (com.appspot.eventorama.shared.model.Activity) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.appspot.eventorama.shared.model.Activity m = (com.appspot.eventorama.shared.model.Activity) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.appspot.eventorama.shared.model.Activity m = (com.appspot.eventorama.shared.model.Activity) model;
        if (m.getApplicationRef() == null) {
            throw new NullPointerException("The property(applicationRef) must not be null.");
        }
        m.getApplicationRef().assignKeyIfNecessary(ds);
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        m.getUserRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
        com.appspot.eventorama.shared.model.Activity m = (com.appspot.eventorama.shared.model.Activity) model;
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
        com.appspot.eventorama.shared.model.Activity m = (com.appspot.eventorama.shared.model.Activity) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getApplicationRef() != null && m.getApplicationRef().getKey() != null){
            writer.setNextPropertyName("applicationRef");
            encoder0.encode(writer, m.getApplicationRef(), maxDepth, currentDepth);
        }
        if(m.getKey() != null){
            writer.setNextPropertyName("key");
            encoder0.encode(writer, m.getKey());
        }
        if(m.getPhotoUrl() != null){
            writer.setNextPropertyName("photoUrl");
            encoder0.encode(writer, m.getPhotoUrl());
        }
        if(m.getText() != null){
            writer.setNextPropertyName("text");
            encoder0.encode(writer, m.getText());
        }
        if(m.getTimestamp() != null){
            writer.setNextPropertyName("timestamp");
            encoder0.encode(writer, m.getTimestamp());
        }
        writer.setNextPropertyName("type");
        encoder0.encode(writer, m.getType());
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
    protected com.appspot.eventorama.shared.model.Activity jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.appspot.eventorama.shared.model.Activity m = new com.appspot.eventorama.shared.model.Activity();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("applicationRef");
        decoder0.decode(reader, m.getApplicationRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("photoUrl");
        m.setPhotoUrl(decoder0.decode(reader, m.getPhotoUrl()));
        reader = rootReader.newObjectReader("text");
        m.setText(decoder0.decode(reader, m.getText()));
        reader = rootReader.newObjectReader("timestamp");
        m.setTimestamp(decoder0.decode(reader, m.getTimestamp()));
        reader = rootReader.newObjectReader("type");
        m.setType(decoder0.decode(reader, m.getType()));
        reader = rootReader.newObjectReader("userRef");
        decoder0.decode(reader, m.getUserRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("version");
        m.setVersion(decoder0.decode(reader, m.getVersion()));
        return m;
    }
}
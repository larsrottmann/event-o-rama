package com.appspot.eventorama.server.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2011-07-06 12:23:58")
/** */
public final class UserMeta extends org.slim3.datastore.ModelMeta<com.appspot.eventorama.shared.model.User> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.User, java.lang.Float> accuracy = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.User, java.lang.Float>(this, "accuracy", "accuracy", java.lang.Float.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.appspot.eventorama.shared.model.User, org.slim3.datastore.ModelRef<com.appspot.eventorama.shared.model.Application>, com.appspot.eventorama.shared.model.Application> applicationRef = new org.slim3.datastore.ModelRefAttributeMeta<com.appspot.eventorama.shared.model.User, org.slim3.datastore.ModelRef<com.appspot.eventorama.shared.model.Application>, com.appspot.eventorama.shared.model.Application>(this, "applicationRef", "applicationRef", org.slim3.datastore.ModelRef.class, com.appspot.eventorama.shared.model.Application.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.User, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.User, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.User, com.google.appengine.api.datastore.GeoPt> location = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.User, com.google.appengine.api.datastore.GeoPt>(this, "location", "location", com.google.appengine.api.datastore.GeoPt.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.User, java.util.Date> locationUpdated = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.User, java.util.Date>(this, "locationUpdated", "locationUpdated", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.User> name = new org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.User>(this, "name", "name");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.User> registrationId = new org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.User>(this, "registrationId", "registrationId");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.User, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.User, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final UserMeta slim3_singleton = new UserMeta();

    /**
     * @return the singleton
     */
    public static UserMeta get() {
       return slim3_singleton;
    }

    /** */
    public UserMeta() {
        super("User", com.appspot.eventorama.shared.model.User.class);
    }

    @Override
    public com.appspot.eventorama.shared.model.User entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.appspot.eventorama.shared.model.User model = new com.appspot.eventorama.shared.model.User();
        model.setAccuracy(doubleToFloat((java.lang.Double) entity.getProperty("accuracy")));
        if (model.getApplicationRef() == null) {
            throw new NullPointerException("The property(applicationRef) is null.");
        }
        model.getApplicationRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("applicationRef"));
        model.setKey(entity.getKey());
        model.setLocation((com.google.appengine.api.datastore.GeoPt) entity.getProperty("location"));
        model.setLocationUpdated((java.util.Date) entity.getProperty("locationUpdated"));
        model.setName((java.lang.String) entity.getProperty("name"));
        model.setRegistrationId((java.lang.String) entity.getProperty("registrationId"));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.appspot.eventorama.shared.model.User m = (com.appspot.eventorama.shared.model.User) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("accuracy", m.getAccuracy());
        if (m.getApplicationRef() == null) {
            throw new NullPointerException("The property(applicationRef) must not be null.");
        }
        entity.setProperty("applicationRef", m.getApplicationRef().getKey());
        entity.setProperty("location", m.getLocation());
        entity.setProperty("locationUpdated", m.getLocationUpdated());
        entity.setProperty("name", m.getName());
        entity.setProperty("registrationId", m.getRegistrationId());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.appspot.eventorama.shared.model.User m = (com.appspot.eventorama.shared.model.User) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.appspot.eventorama.shared.model.User m = (com.appspot.eventorama.shared.model.User) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.appspot.eventorama.shared.model.User m = (com.appspot.eventorama.shared.model.User) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.appspot.eventorama.shared.model.User m = (com.appspot.eventorama.shared.model.User) model;
        if (m.getApplicationRef() == null) {
            throw new NullPointerException("The property(applicationRef) must not be null.");
        }
        m.getApplicationRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
        com.appspot.eventorama.shared.model.User m = (com.appspot.eventorama.shared.model.User) model;
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
        com.appspot.eventorama.shared.model.User m = (com.appspot.eventorama.shared.model.User) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getAccuracy() != null){
            writer.setNextPropertyName("accuracy");
            encoder0.encode(writer, m.getAccuracy());
        }
        if(m.getApplicationRef() != null && m.getApplicationRef().getKey() != null){
            writer.setNextPropertyName("applicationRef");
            encoder0.encode(writer, m.getApplicationRef(), maxDepth, currentDepth);
        }
        if(m.getKey() != null){
            writer.setNextPropertyName("key");
            encoder0.encode(writer, m.getKey());
        }
        if(m.getLocation() != null){
            writer.setNextPropertyName("location");
            encoder0.encode(writer, m.getLocation());
        }
        if(m.getLocationUpdated() != null){
            writer.setNextPropertyName("locationUpdated");
            encoder0.encode(writer, m.getLocationUpdated());
        }
        if(m.getName() != null){
            writer.setNextPropertyName("name");
            encoder0.encode(writer, m.getName());
        }
        if(m.getRegistrationId() != null){
            writer.setNextPropertyName("registrationId");
            encoder0.encode(writer, m.getRegistrationId());
        }
        if(m.getVersion() != null){
            writer.setNextPropertyName("version");
            encoder0.encode(writer, m.getVersion());
        }
        writer.endObject();
    }

    @Override
    protected com.appspot.eventorama.shared.model.User jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.appspot.eventorama.shared.model.User m = new com.appspot.eventorama.shared.model.User();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("accuracy");
        m.setAccuracy(decoder0.decode(reader, m.getAccuracy()));
        reader = rootReader.newObjectReader("applicationRef");
        decoder0.decode(reader, m.getApplicationRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("location");
        m.setLocation(decoder0.decode(reader, m.getLocation()));
        reader = rootReader.newObjectReader("locationUpdated");
        m.setLocationUpdated(decoder0.decode(reader, m.getLocationUpdated()));
        reader = rootReader.newObjectReader("name");
        m.setName(decoder0.decode(reader, m.getName()));
        reader = rootReader.newObjectReader("registrationId");
        m.setRegistrationId(decoder0.decode(reader, m.getRegistrationId()));
        reader = rootReader.newObjectReader("version");
        m.setVersion(decoder0.decode(reader, m.getVersion()));
        return m;
    }
}
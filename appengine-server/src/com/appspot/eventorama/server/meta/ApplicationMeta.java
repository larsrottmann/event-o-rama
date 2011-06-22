package com.appspot.eventorama.server.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2011-06-21 20:10:11")
/** */
public final class ApplicationMeta extends org.slim3.datastore.ModelMeta<com.appspot.eventorama.shared.model.Application> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, java.lang.Boolean> active = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, java.lang.Boolean>(this, "active", "active", boolean.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.Application> downloadUrl = new org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.Application>(this, "downloadUrl", "downloadUrl");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, java.util.Date> expirationDate = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, java.util.Date>(this, "expirationDate", "expirationDate", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.Application> packageName = new org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.Application>(this, "packageName", "packageName");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, java.util.Date> startDate = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, java.util.Date>(this, "startDate", "startDate", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.Application> title = new org.slim3.datastore.StringAttributeMeta<com.appspot.eventorama.shared.model.Application>(this, "title", "title");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, com.google.appengine.api.users.User> user = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, com.google.appengine.api.users.User>(this, "user", "user", com.google.appengine.api.users.User.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<com.appspot.eventorama.shared.model.Application, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final ApplicationMeta slim3_singleton = new ApplicationMeta();

    /**
     * @return the singleton
     */
    public static ApplicationMeta get() {
       return slim3_singleton;
    }

    /** */
    public ApplicationMeta() {
        super("Application", com.appspot.eventorama.shared.model.Application.class);
    }

    @Override
    public com.appspot.eventorama.shared.model.Application entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.appspot.eventorama.shared.model.Application model = new com.appspot.eventorama.shared.model.Application();
        model.setActive(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("active")));
        model.setDownloadUrl((java.lang.String) entity.getProperty("downloadUrl"));
        model.setExpirationDate((java.util.Date) entity.getProperty("expirationDate"));
        model.setKey(entity.getKey());
        model.setPackageName((java.lang.String) entity.getProperty("packageName"));
        model.setStartDate((java.util.Date) entity.getProperty("startDate"));
        model.setTitle((java.lang.String) entity.getProperty("title"));
        model.setUser((com.google.appengine.api.users.User) entity.getProperty("user"));
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.appspot.eventorama.shared.model.Application m = (com.appspot.eventorama.shared.model.Application) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("active", m.isActive());
        entity.setProperty("downloadUrl", m.getDownloadUrl());
        entity.setProperty("expirationDate", m.getExpirationDate());
        entity.setProperty("packageName", m.getPackageName());
        entity.setProperty("startDate", m.getStartDate());
        entity.setProperty("title", m.getTitle());
        entity.setProperty("user", m.getUser());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.appspot.eventorama.shared.model.Application m = (com.appspot.eventorama.shared.model.Application) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.appspot.eventorama.shared.model.Application m = (com.appspot.eventorama.shared.model.Application) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        com.appspot.eventorama.shared.model.Application m = (com.appspot.eventorama.shared.model.Application) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
        com.appspot.eventorama.shared.model.Application m = (com.appspot.eventorama.shared.model.Application) model;
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
        com.appspot.eventorama.shared.model.Application m = (com.appspot.eventorama.shared.model.Application) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        writer.setNextPropertyName("active");
        encoder0.encode(writer, m.isActive());
        if(m.getDownloadUrl() != null){
            writer.setNextPropertyName("downloadUrl");
            encoder0.encode(writer, m.getDownloadUrl());
        }
        if(m.getExpirationDate() != null){
            writer.setNextPropertyName("expirationDate");
            encoder0.encode(writer, m.getExpirationDate());
        }
        if(m.getKey() != null){
            writer.setNextPropertyName("key");
            encoder0.encode(writer, m.getKey());
        }
        if(m.getPackageName() != null){
            writer.setNextPropertyName("packageName");
            encoder0.encode(writer, m.getPackageName());
        }
        if(m.getStartDate() != null){
            writer.setNextPropertyName("startDate");
            encoder0.encode(writer, m.getStartDate());
        }
        if(m.getTitle() != null){
            writer.setNextPropertyName("title");
            encoder0.encode(writer, m.getTitle());
        }
        if(m.getUser() != null){
            writer.setNextPropertyName("user");
            encoder0.encode(writer, m.getUser());
        }
        if(m.getVersion() != null){
            writer.setNextPropertyName("version");
            encoder0.encode(writer, m.getVersion());
        }
        writer.endObject();
    }

    @Override
    protected com.appspot.eventorama.shared.model.Application jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.appspot.eventorama.shared.model.Application m = new com.appspot.eventorama.shared.model.Application();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("active");
        m.setActive(decoder0.decode(reader, m.isActive()));
        reader = rootReader.newObjectReader("downloadUrl");
        m.setDownloadUrl(decoder0.decode(reader, m.getDownloadUrl()));
        reader = rootReader.newObjectReader("expirationDate");
        m.setExpirationDate(decoder0.decode(reader, m.getExpirationDate()));
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("packageName");
        m.setPackageName(decoder0.decode(reader, m.getPackageName()));
        reader = rootReader.newObjectReader("startDate");
        m.setStartDate(decoder0.decode(reader, m.getStartDate()));
        reader = rootReader.newObjectReader("title");
        m.setTitle(decoder0.decode(reader, m.getTitle()));
        reader = rootReader.newObjectReader("user");
        m.setUser(decoder0.decode(reader, m.getUser()));
        reader = rootReader.newObjectReader("version");
        m.setVersion(decoder0.decode(reader, m.getVersion()));
        return m;
    }
}
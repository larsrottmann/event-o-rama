package com.appspot.eventorama.shared.model;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.IsSerializable;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

@Model(schemaVersion = 1)
public class User implements Serializable, IsSerializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;

    private ModelRef<Application> applicationRef = new ModelRef<Application>(Application.class);
    private String name;
    private String registrationId;
    private GeoPt location;
    private Date locationUpdated;
    private Float accuracy;
    
    
    /**
     * Returns the key.
     *
     * @return the key
     */
    public Key getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the key
     */
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * Returns the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version
     *            the version
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    /**
     * @return the applicationRef
     */
    public ModelRef<Application> getApplicationRef() {
        return applicationRef;
    }

    /**
     * @return the location
     */
    public GeoPt getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(GeoPt location) {
        this.location = location;
    }

    /**
     * @return the locationUpdated
     */
    public Date getLocationUpdated() {
        return locationUpdated;
    }

    /**
     * @param locationUpdated the locationUpdated to set
     */
    public void setLocationUpdated(Date locationUpdated) {
        this.locationUpdated = locationUpdated;
    }

    /**
     * @return the accuracy
     */
    public Float getAccuracy() {
        return accuracy;
    }

    /**
     * @param accuracy the accuracy to set
     */
    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [key=");
        builder.append(key);
        builder.append(", version=");
        builder.append(version);
        builder.append(", applicationRef=");
        builder.append(applicationRef);
        builder.append(", name=");
        builder.append(name);
        builder.append(", registrationId=");
        builder.append(getRegistrationId());
        builder.append(", location=");
        builder.append(location);
        builder.append(", locationUpdated=");
        builder.append(locationUpdated);
        builder.append(", accuracy=");
        builder.append(accuracy);
        builder.append("]");
        return builder.toString();
    }
    
    
}

package com.appspot.eventorama.shared.model;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.utils.SystemProperty;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

@Model(schemaVersion = 1)
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;

    private User user;
    private String title;
    private boolean active;
    private String packageName;
    private Date startDate;
    private Date expirationDate;
    private String downloadUrl;
    
    
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

    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
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
        Application other = (Application) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }


    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }


    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public String getLocalDownloadUrl() {
        String hostName = "eventorama.appspot.com";
        if (SystemProperty.environment.value() ==
            SystemProperty.Environment.Value.Development) {
            // The app is not running on App Engine...
            hostName = "localhost:8888";
        }
        return "http://" + hostName + "/download/" + KeyFactory.keyToString(getKey());
    }


    public void setUser(User user) {
        this.user = user;
    }


    public User getUser() {
        return user;
    }


    public void setActive(boolean active) {
        this.active = active;
    }


    public boolean isActive() {
        return active;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Application [key=");
        builder.append(key);
        builder.append(", version=");
        builder.append(version);
        builder.append(", user=");
        builder.append(user);
        builder.append(", title=");
        builder.append(title);
        builder.append(", active=");
        builder.append(active);
        builder.append(", packageName=");
        builder.append(packageName);
        builder.append(", startDate=");
        builder.append(startDate);
        builder.append(", expirationDate=");
        builder.append(expirationDate);
        builder.append(", downloadUrl=");
        builder.append(downloadUrl);
        builder.append("]");
        return builder.toString();
    }
    
    
}

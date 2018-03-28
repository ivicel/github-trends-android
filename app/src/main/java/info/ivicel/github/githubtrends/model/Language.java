package info.ivicel.github.githubtrends.model;


import android.support.annotation.NonNull;

import java.io.Serializable;

public class Language implements Serializable {
    private String name;
    private String path;
    
    public Language(String name, String path) {
        this.name = name;
        this.path = path;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Language &&
                this.name.equalsIgnoreCase(((Language)obj).getName()) &&
                this.getPath().equalsIgnoreCase(((Language)obj).getPath());
    }
    
    @Override
    public String toString() {
        return "{\"" + name + ":" + path + "\"}";
    }
}

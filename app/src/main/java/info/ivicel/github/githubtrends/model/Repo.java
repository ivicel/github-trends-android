package info.ivicel.github.githubtrends.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import info.ivicel.github.githubtrends.util.HexUtil;

public class Repo {
    @SerializedName("contributors")
    public List<Contributor> contributors;
    
    @SerializedName("des")
    public String description;
    
    @SerializedName("link")
    public String link;
    
    @SerializedName("name")
    public String name;
    
    @SerializedName("owner")
    public String owner;
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Repo && HexUtil.bytesToHex(link.getBytes()).equals(
                HexUtil.bytesToHex(((Repo)obj).link.getBytes()));
    }
}

package com.teketys.templetickets.entities.drawerMenu;

/**
 * Created by rudram1 on 8/25/16.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DrawerItemCategory {

    @SerializedName("parent_id")
    private long id;

    @SerializedName("category_id")
    private long originalId;
    private String name;
    private List<DrawerItemCategory> categories;
    private String type;

    public DrawerItemCategory() {
    }

    public DrawerItemCategory(long id, long originalId, String name) {
        this.id = id;
        this.originalId = originalId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(long originalId) {
        this.originalId = originalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DrawerItemCategory> getChildren() {
        return categories;
    }

    public void setChildren(List<DrawerItemCategory> categories) {
        this.categories = categories;
    }

    public String getType() {
        return "category";
    }

    public void setType(String type) {
        this.type = "category";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrawerItemCategory that = (DrawerItemCategory) o;

        if (id != that.id) return false;
        if (originalId != that.originalId) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (categories != null ? !categories.equals(that.categories) : that.categories != null)
            return false;
        return !(type != null ? !type.equals(that.type) : that.type != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (originalId ^ (originalId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (categories != null ? categories.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DrawerItemCategory{" +
                "id=" + id +
                ", originalId=" + originalId +
                ", name='" + name + '\'' +
                ", children=" + categories +
                ", type='" + type + '\'' +
                '}';
    }

    public boolean hasChildren() {
        return categories != null && !categories.isEmpty();
    }
}

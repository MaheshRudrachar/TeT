package com.teketys.templetickets.entities.drawerMenu;

/**
 * Created by rudram1 on 8/25/16.
 */

public class DrawerItemPage {

    private long id;
    private String title;

    public DrawerItemPage(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public DrawerItemPage() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawerItemPage)) return false;

        DrawerItemPage that = (DrawerItemPage) o;

        if (getId() != that.getId()) return false;
        return !(getName() != null ? !getName().equals(that.getName()) : that.getName() != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DrawerItemPage{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}

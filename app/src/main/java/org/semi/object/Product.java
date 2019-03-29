package org.semi.object;

import java.io.Serializable;

public class Product implements IHaveIdAndName<String>, Serializable {
    private String id;
    private Store store;
    private String title;
    private String name;
    private String description;
    private long cost;
    private String imageURL;
    private Type type;
    public static class Type implements IHaveIdAndName<Integer>, Serializable {
        private int id;
        private String name;
        public Type() {}
        public Type(int id, String name) {
            this.id = id;
            this.name = name;
        }
        @Override
        public Integer getId() {
            return id;
        }
        @Override
        public void setId(Integer id) {
            this.id = id;
        }
        @Override
        public String getName() {
            return name;
        }
        @Override
        public void setName(String name) {
            this.name = name;
        }
        @Override
        public String toString() {return this.name;}
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String fullName) {
        this.name = fullName;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

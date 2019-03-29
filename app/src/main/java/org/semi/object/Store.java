package org.semi.object;

import java.io.Serializable;
import java.util.List;

public class Store implements IHaveIdAndName<String>, Serializable {
    private String id;
    private String title;
    private String name;
    private String description;
    private Type type;
    private List<Utility> utilities;
    private Address address;
    private float rating;
    private Location geo;
    private String contact;
    private String imageURL;
    private String startEnd;
    private List<Product> products;
    private int numComments;
    private int numPoints;
    private int numProducts;

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
        public String toString() {return Type.this.name;}
    }

    public static class Utility implements IHaveIdAndName<Integer>, Serializable {
        private int id;
        private String name;
        public Utility() {}
        public Utility(int id, String name) {
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
        public String toString() {return Utility.this.name;}
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String fullName) {
        this.name = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Utility> getUtilities() {
        return utilities;
    }

    public void setUtilities(List<Utility> utilities) {
        this.utilities = utilities;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Location getGeo() {
        return geo;
    }

    public void setGeo(Location geo) {
        this.geo = geo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStartEnd() {
        return startEnd;
    }

    public void setStartEnd(String startEnd) {
        this.startEnd = startEnd;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public int getNumPoints() {
        return numPoints;
    }

    public void setNumPoints(int numPoints) {
        this.numPoints = numPoints;
    }

    public int getNumProducts() {
        return numProducts;
    }

    public void setNumProducts(int numProducts) {
        this.numProducts = numProducts;
    }
}

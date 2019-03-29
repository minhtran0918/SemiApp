package org.semi.object;

import java.io.Serializable;

public class District implements IHaveIdAndName<Integer>, Serializable {
    private int id;
    private String name;

    public District() {}

    public District(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public District(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

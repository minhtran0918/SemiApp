package org.semi.object;

import java.io.Serializable;

public class Town implements IHaveIdAndName<Integer>, Serializable {
    private int id;
    private String name;

    public Town() {}

    public Town(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Town(String name) {
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

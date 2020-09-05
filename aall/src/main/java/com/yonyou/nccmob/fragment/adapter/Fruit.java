package com.yonyou.nccmob.fragment.adapter;

public class Fruit {

    private String name;
    private int imageId;

    public Fruit(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;

    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Fruit{");
        sb.append("name='").append(name).append('\'');
        sb.append(", imageId=").append(imageId);
        sb.append('}');
        return sb.toString();
    }
}
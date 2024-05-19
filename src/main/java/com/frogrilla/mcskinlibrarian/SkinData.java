package com.frogrilla.mcskinlibrarian;

public class SkinData {
    public String capeId;
    public String created;
    public String id;
    public String modelImage;
    public String name;
    public String skinImage;
    public boolean slim;
    public String textureId;
    public String updated;

    public SkinData(String capeId, String created, String id, String modelImage, String name, String skinImage, boolean slim, String textureId, String updated) {
        this.capeId = capeId;
        this.created = created;
        this.id = id;
        this.modelImage = modelImage;
        this.name = name;
        this.skinImage = skinImage;
        this.slim = slim;
        this.textureId = textureId;
        this.updated = updated;
    }

    public SkinData(){
        this.capeId = "";
        this.created = "";
        this.id = "";
        this.modelImage = "";
        this.name = "";
        this.skinImage = "";
        this.slim = false;
        this.textureId = "";
        this.updated = "";
    }

    public String toString(){
        return this.id;
    }
}

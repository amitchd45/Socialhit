package com.socialhit.app.modelsClass;

public class ModelsUser {

    String name,email,search,phone,image,cover,uid;

    public ModelsUser(String name, String email, String search, String phone, String image, String cover, String uid) {
        this.name = name;
        this.email = email;
        this.search = search;
        this.phone = phone;
        this.image = image;
        this.cover = cover;
        this.uid = uid;
    }

    public ModelsUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModelsUser){
            ModelsUser user= (ModelsUser) obj;
            return this.uid.equals(user.getUid());
        }else
            return false;
    }
}

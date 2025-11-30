package vn.hcmute.viewflipper_cricleindicator.models;

import java.io.Serializable;

public class Images implements Serializable {
    private int imageId;

    public Images(int imageId) {
        this.imageId = imageId;
    }

    public int getImagesId() {
        return imageId;
    }

    public void setimageId(int imageId) {
        this.imageId = imageId;
    }
}


package com.learning.firhan.aquacare.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class FishModel implements Parcelable{
    private int Id;
    private int aquariumId;
    private String imageUri;
    private String imageThumbnailUri;
    private String name;
    private String type;
    private String description;
    private String purchaseDate;

    protected FishModel(Parcel in) {
        Id = in.readInt();
        aquariumId = in.readInt();
        imageUri = in.readString();
        imageThumbnailUri = in.readString();
        name = in.readString();
        type = in.readString();
        description = in.readString();
        purchaseDate = in.readString();
    }

    public static final Creator<FishModel> CREATOR = new Creator<FishModel>() {
        @Override
        public FishModel createFromParcel(Parcel in) {
            return new FishModel(in);
        }

        @Override
        public FishModel[] newArray(int size) {
            return new FishModel[size];
        }
    };

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getAquariumIdId() {
        return aquariumId;
    }

    public void setAquariumIdId(int aquariumId) {
        this.aquariumId = aquariumId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageThumbnailUri() {
        return imageThumbnailUri;
    }

    public void setImageThumbnailUri(String imageThumbnailUri) {
        this.imageThumbnailUri = imageThumbnailUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public FishModel(int id, int aquariumId, String imageUri, String imageThumbnailUri, String name, String type, String description, String purchaseDate) {

        Id = id;
        this.aquariumId = aquariumId;
        this.imageUri = imageUri;
        this.imageThumbnailUri = imageThumbnailUri;
        this.name = name;
        this.type = type;
        this.description = description;
        this.purchaseDate = purchaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeInt(aquariumId);
        dest.writeString(imageUri);
        dest.writeString(imageThumbnailUri);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeString(purchaseDate);
    }
}

package com.learning.firhan.aquacare.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class AquariumModel implements Parcelable {
    private int id;
    private String imageThumbnailUri;
    private String imageUri;
    private String name;
    private String description;
    private double aquariumLength;
    private double aquariumHeight;
    private double aquariumWide;

    public AquariumModel(int id, String imageThumbnailUri, String imageUri, String name, String description, double aquariumLength, double aquariumHeight, double aquariumWide) {
        this.id = id;
        this.name = name;
        this.imageUri = imageUri;
        this.imageThumbnailUri = imageThumbnailUri;
        this.description = description;
        this.aquariumLength = aquariumLength;
        this.aquariumHeight = aquariumHeight;
        this.aquariumWide = aquariumWide;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageThumbnailUri() {
        return imageThumbnailUri;
    }

    public void setImageThumbnailUri(String imageThumbnailUri) {
        this.imageThumbnailUri = imageThumbnailUri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAquariumLength() {
        return aquariumLength;
    }

    public void setAquariumLength(double aquariumLength) {
        this.aquariumLength = aquariumLength;
    }

    public double getAquariumHeight() {
        return aquariumHeight;
    }

    public void setAquariumHeight(double aquariumHeight) {
        this.aquariumHeight = aquariumHeight;
    }

    public double getAquariumWide() {
        return aquariumWide;
    }

    public void setAquariumWide(double aquariumWide) {
        this.aquariumWide = aquariumWide;
    }

    protected AquariumModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        imageUri = in.readString();
        imageThumbnailUri = in.readString();
        description = in.readString();
        aquariumLength = in.readDouble();
        aquariumHeight = in.readDouble();
        aquariumWide = in.readDouble();
    }

    public static final Creator<AquariumModel> CREATOR = new Creator<AquariumModel>() {
        @Override
        public AquariumModel createFromParcel(Parcel in) {
            return new AquariumModel(in);
        }

        @Override
        public AquariumModel[] newArray(int size) {
            return new AquariumModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(imageUri);
        dest.writeString(imageThumbnailUri);
        dest.writeString(description);
        dest.writeDouble(aquariumLength);
        dest.writeDouble(aquariumHeight);
        dest.writeDouble(aquariumWide);
    }
}

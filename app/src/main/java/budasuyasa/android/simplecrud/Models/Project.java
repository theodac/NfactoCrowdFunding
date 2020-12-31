package budasuyasa.android.simplecrud.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asha on 4/23/2018.
 * Class to mapping book object implement Parcelable
 * karena akan digunakan untuk passing object Book antar activity
 */

public class Project implements Parcelable{

    String id;
    String title;
    String picture;
    String description;
    String montant;

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    String end_date;
    String id_user;


    protected Project(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        picture = in.readString();
        montant = in.readString();
        end_date = in.readString();
        id_user = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(picture);
        dest.writeString(montant);
        dest.writeString(end_date);
        dest.writeString(id_user);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
}

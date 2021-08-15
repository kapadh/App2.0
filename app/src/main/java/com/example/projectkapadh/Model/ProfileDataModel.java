package com.example.projectkapadh.Model;

public class ProfileDataModel {
    String ProfileCoverLink, ProfilePicLink, FullName, MobileNumber, Email, Address, isMobileVerified, isEmailVerified;

    ProfileDataModel(){

    }

    public ProfileDataModel(String profileCoverLink, String profilePicLink, String fullName, String mobileNumber, String email, String address, String isMobileVerified, String isEmailVerified) {
        ProfileCoverLink = profileCoverLink;
        ProfilePicLink = profilePicLink;
        FullName = fullName;
        MobileNumber = mobileNumber;
        Email = email;
        Address = address;
        this.isMobileVerified = isMobileVerified;
        this.isEmailVerified = isEmailVerified;
    }

    public String getProfileCoverLink() {
        return ProfileCoverLink;
    }

    public void setProfileCoverLink(String profileCoverLink) {
        ProfileCoverLink = profileCoverLink;
    }

    public String getProfilePicLink() {
        return ProfilePicLink;
    }

    public void setProfilePicLink(String profilePicLink) {
        ProfilePicLink = profilePicLink;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getIsMobileVerified() {
        return isMobileVerified;
    }

    public void setIsMobileVerified(String isMobileVerified) {
        this.isMobileVerified = isMobileVerified;
    }

    public String getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(String isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }
}

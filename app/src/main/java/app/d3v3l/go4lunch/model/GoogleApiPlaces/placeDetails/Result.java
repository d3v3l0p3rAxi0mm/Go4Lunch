
package app.d3v3l.go4lunch.model.GoogleApiPlaces.placeDetails;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("address_components")
    @Expose
    private List<AddressComponent> addressComponents;
    @SerializedName("adr_address")
    @Expose
    private String adrAddress;
    @SerializedName("business_status")
    @Expose
    private String businessStatus;
    @SerializedName("delivery")
    @Expose
    private Boolean delivery;
    @SerializedName("dine_in")
    @Expose
    private Boolean dineIn;
    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("icon_background_color")
    @Expose
    private String iconBackgroundColor;
    @SerializedName("icon_mask_base_uri")
    @Expose
    private String iconMaskBaseUri;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("plus_code")
    @Expose
    private PlusCode plusCode;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("reviews")
    @Expose
    private List<Review> reviews;
    @SerializedName("serves_beer")
    @Expose
    private Boolean servesBeer;
    @SerializedName("serves_lunch")
    @Expose
    private Boolean servesLunch;
    @SerializedName("serves_wine")
    @Expose
    private Boolean servesWine;
    @SerializedName("types")
    @Expose
    private List<String> types;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("user_ratings_total")
    @Expose
    private Integer userRatingsTotal;
    @SerializedName("utc_offset")
    @Expose
    private Integer utcOffset;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;

    public List<AddressComponent> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(List<AddressComponent> addressComponents) {
        this.addressComponents = addressComponents;
    }

    public String getAdrAddress() {
        return adrAddress;
    }

    public void setAdrAddress(String adrAddress) {
        this.adrAddress = adrAddress;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public Boolean getDelivery() {
        return delivery;
    }

    public void setDelivery(Boolean delivery) {
        this.delivery = delivery;
    }

    public Boolean getDineIn() {
        return dineIn;
    }

    public void setDineIn(Boolean dineIn) {
        this.dineIn = dineIn;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconBackgroundColor() {
        return iconBackgroundColor;
    }

    public void setIconBackgroundColor(String iconBackgroundColor) {
        this.iconBackgroundColor = iconBackgroundColor;
    }

    public String getIconMaskBaseUri() {
        return iconMaskBaseUri;
    }

    public void setIconMaskBaseUri(String iconMaskBaseUri) {
        this.iconMaskBaseUri = iconMaskBaseUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public PlusCode getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(PlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Boolean getServesBeer() {
        return servesBeer;
    }

    public void setServesBeer(Boolean servesBeer) {
        this.servesBeer = servesBeer;
    }

    public Boolean getServesLunch() {
        return servesLunch;
    }

    public void setServesLunch(Boolean servesLunch) {
        this.servesLunch = servesLunch;
    }

    public Boolean getServesWine() {
        return servesWine;
    }

    public void setServesWine(Boolean servesWine) {
        this.servesWine = servesWine;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(Integer userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public Integer getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(Integer utcOffset) {
        this.utcOffset = utcOffset;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

}

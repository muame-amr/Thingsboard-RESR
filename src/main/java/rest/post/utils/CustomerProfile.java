package rest.post.utils;

public class CustomerProfile {
    String customerName;
    String deviceName;
    String accessToken;

    public CustomerProfile() {
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

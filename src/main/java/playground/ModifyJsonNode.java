package playground;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.thingsboard.rest.client.RestClient;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import rest.post.utils.CreateCustomerDevice;
import rest.post.utils.CustomerProfile;

import java.io.IOException;

import static org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo.mapper;

public class ModifyJsonNode {

    public static void main(String[] args) throws IOException {
        // ThingsBoard REST API URL
        String url = "http://localhost:8008";

        // Default Tenant Administrator credentials
        String username = "tenant@thingsboard.org";
        String password = "tenant";

        // Creating new rest client and auth with credentials
        RestClient client = new RestClient(url);
        client.login(username, password);

        // Assign each customers to respective devices
        String customerTitle = "DummyCust";
        String deviceName = "DummyDevice";
        String deviceID = "";

        // Creating a customer
        Customer customer = new Customer();
        System.out.println(customerTitle);
        customer.setTitle(customerTitle);
        customer = client.saveCustomer(customer);

        // Creating a Device
        Device device = new Device();
        System.out.println(deviceName);
        device.setName(deviceName);
        device.setType("Hardware");

        // Assign customer to device
        device.setCustomerId(customer.getId());
        device = client.saveDevice(device);

        JsonNode dashboardJson = mapper.readTree(ModifyJsonNode.class.getClassLoader().getResourceAsStream("dummydashboard.json"));
        JsonNode nodeParent = dashboardJson.get("configuration").get("entityAliases").get("c699cfa1-0519-2cba-ffd3-dfcb04ce8753")
                .get("filter").get("singleEntity");
        System.out.println(nodeParent.asText());
        deviceID = device.getId().toString();
        System.out.println("Device ID: " + deviceID);
        ((ObjectNode) nodeParent).put("id", deviceID);
        System.out.println(dashboardJson);

        Dashboard dashboard = new Dashboard();
        dashboard.setTitle("DummyDashboard");
        dashboard.setConfiguration(dashboardJson.get("configuration"));

        // Assign dashboard to customer;
        dashboard.addAssignedCustomer(customer);
        dashboard = client.saveDashboard(dashboard);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        String postUrl = "http://localhost:8008/api/v1/" + client.getDeviceCredentialsByDeviceId(device.getId()).get().getCredentialsId() + "/telemetry";
        HttpPost httpPost = new HttpPost(postUrl);
        StringEntity params = new StringEntity("{\"Memory\":\"4.8\", \"Cpu\":\"65\", \"Temp\":\"55.6\"}");
        httpPost.addHeader("content-type", "application/json");
        httpPost.setEntity(params);
        HttpResponse response = httpclient.execute(httpPost);
    }
}
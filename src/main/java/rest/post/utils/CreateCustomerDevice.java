package rest.post.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.thingsboard.rest.client.RestClient;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.security.DeviceCredentials;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo.mapper;

public class CreateCustomerDevice {
    private String url;
    private String username;
    private String password;
    RestClient client;

    public CreateCustomerDevice(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private void connect() {
        // Creating new rest client and auth with credentials
        client = new RestClient(url);
        client.login(username, password);
    }

    public ArrayList<CustomerProfile> createAndAssign(ArrayList<CustomerProfile> users) throws IOException {
        // Connect to service
        connect();

        // Create a container to store customer and device info
        ArrayList<Customer> allCustomers = new ArrayList<>();
        ArrayList<Device> allDevices = new ArrayList<>();

        // Assign each customers to respective devices
        for (CustomerProfile user : users) {
            String customerTitle = user.customerName;
            String deviceName = user.deviceName;

            // Creating a customer
            Customer customer = new Customer();
            System.out.println(customerTitle);
            customer.setTitle(customerTitle);
            customer = client.saveCustomer(customer);
            allCustomers.add(customer);

            // Creating a Device
            Device device = new Device();
            System.out.println(deviceName);
            device.setName(deviceName);
            device.setType("Hardware");

            // Assign customer to device
            device.setCustomerId(customer.getId());
            device = client.saveDevice(device);
            allDevices.add(device);

            // Update device token
            DeviceCredentials deviceCredentials = client.getDeviceCredentialsByDeviceId(device.getId()).get();
            user.setAccessToken(deviceCredentials.getCredentialsId());
        }

        // Get dashboard template and Entity List Json node
        JsonNode dashboardJson = mapper.readTree(CreateCustomerDevice.class.getClassLoader().getResourceAsStream("dashboard.json"));
        JsonNode entityListNode = dashboardJson.get("configuration").get("entityAliases").get("1057e271-5698-bc95-4234-2eb894c91c1d")
                .get("filter");

        // String of Json Node for single entity
        String[] entityField = {"57f00db3-458b-bb96-22d2-146327113bfc", "da6857c0-43dc-7e5f-4cb0-5235ccbf9c7c"};
//        ArrayList<String> allDeviceID = new ArrayList<>();

        for(int i = 0; i < allDevices.size(); ++i) {
            String deviceID = allDevices.get(i).getId().toString();
//            allDeviceID.add(deviceID);
            System.out.println("Device " + (i + 1) + ": " + deviceID);
            // For EntityList
            ((ArrayNode) entityListNode.get("entityList")).add(deviceID);
            // For Single Entity
            System.out.println("Entity Field " + i + ": " + entityField[i]);
            JsonNode singleEntityNode = dashboardJson.get("configuration").get("entityAliases").get(entityField[i])
                    .get("filter").get("singleEntity");
            ((ObjectNode) singleEntityNode).put("id", deviceID);
        }
        System.out.println(dashboardJson.toPrettyString());

        // Assign dashboard to customer
        Dashboard dashboard = new Dashboard();
        dashboard.setTitle("Resources Monitor");
        dashboard.setConfiguration(dashboardJson.get("configuration"));
        Dashboard finalDashboard = dashboard;
        allCustomers.forEach(customer -> {
            finalDashboard.addAssignedCustomer(customer);
//            client.assignDashboardToCustomer(customer.getId(), finalDashboard.getId());
        });
        dashboard = client.saveDashboard(finalDashboard);

        return users;
    }
}
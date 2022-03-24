package playground;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.thingsboard.rest.client.RestClient;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.Dashboard;
import org.thingsboard.server.common.data.DashboardInfo;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.device.data.DeviceConfiguration;
import org.thingsboard.server.common.data.device.data.DeviceData;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.io.IOException;

import static org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo.mapper;

public class FetchTenantData {
    public static void main(String[] args) throws IOException {

        // ThingsBoard REST API URL
        String url = "http://localhost:8008";

        // Default Tenant Administrator credentials
        String username = "tenant@thingsboard.org";
        String password = "tenant";

        // Creating new rest client and auth with credentials
        RestClient client = new RestClient(url);
        client.login(username, password);

//        JsonNode jsonNode = mapper.readTree(rest.post.utils.CreateCustomerDevice.class.getClassLoader().getResourceAsStream("hw_monitor.json"));
//        String json = mapper.writeValueAsString();
//        System.out.println(json);

        // Creating a customer
//        Customer customer = new Customer();
//        customer.setTitle("DummyCust");
//        customer = client.saveCustomer(customer);
//        System.out.println(customer.getId());
//
//        // Creating a Device
//        Device device = new Device();
//        device.setName("DummyDevice");
//        device = client.saveDevice(device);
//        DeviceData deviceData = new DeviceData();
//        System.out.println(device.getId());

        // Perform logout of current user and close the client
        client.logout();
        client.close();
    }
}

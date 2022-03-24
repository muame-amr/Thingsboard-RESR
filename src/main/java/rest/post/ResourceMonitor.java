package rest.post;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.management.OperatingSystemMXBean;
import org.json.JSONObject;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import rest.post.utils.CreateCustomerDevice;
import rest.post.utils.CustomerDeviceLoader;
import rest.post.utils.CustomerProfile;
import rest.post.utils.PostTelemetry;

public class ResourceMonitor {

    private static OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {

        // ThingsBoard REST API URL
        String url = "http://localhost:8008";

        // Default Tenant Administrator credentials
        String username = "tenant@thingsboard.org";
        String password = "tenant";

        // Get users detail from text file
        String fileName = "tenantCustomer.txt";
        URL res = ResourceMonitor.class.getClassLoader().getResource(fileName);
        File file = Paths.get(res.toURI()).toFile();
        CustomerDeviceLoader loader = new CustomerDeviceLoader(file);
        ArrayList<CustomerProfile> users = loader.getUser();

        // Create customer and device and assign them respectively
        CreateCustomerDevice runner = new CreateCustomerDevice(url, username, password);
        users = runner.createAndAssign(users);

        while (true) {
            Thread.sleep(3000);
            System.out.println("Access Token : " + users.get(0).getAccessToken());
            monitor(users.get(0).getAccessToken());
            System.out.println("Access Token : " + users.get(1).getAccessToken());
            monitor(users.get(1).getAccessToken());
        }

    }

    public static double getMemory() {
        double totalMemory = systemMXBean.getTotalPhysicalMemorySize();
        double freeMemory = systemMXBean.getFreePhysicalMemorySize();
        double usedMemory = (totalMemory - freeMemory) / 1073741824;
        return usedMemory;
    }

    public static double getCpu() {
        return (systemMXBean.getSystemCpuLoad() + systemMXBean.getProcessCpuLoad()) * 100;
    }

    public static double getTemp() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        Sensors sensors = hal.getSensors();
        return sensors.getCpuTemperature();
    }

    public static void postClient(JSONObject json, String token) throws IOException {
        PostTelemetry postClient = new PostTelemetry(token);
        postClient.setParams(json);
        postClient.execute();
    }

    public static void monitor(String token) throws IOException, InterruptedException {
        Map<String, String> data = new HashMap<>();

        // Get Memory Usage
        double usedMemory = getMemory();
        System.out.printf("Memory Usage: %.2fGB\n", usedMemory);
        data.put("Memory", String.format("%.2f",usedMemory));

        // Get CPU Usage
        double cpuUsage = getCpu();
        System.out.printf("CPU Usage: %.0f%%\n", cpuUsage);
        data.put("Cpu", String.format("%.2f",cpuUsage));

        // Get CPU Temp
        double temp = getTemp();
        System.out.printf("CPU Temp: %.2f°C\n", temp);
        data.put("Temp", String.format("%.2f",temp));

        // Convert Data to JSON
        JSONObject json = new JSONObject(data);
        System.out.println(json);

        // Post Data to dashboard
        postClient(json, token);
    }
}


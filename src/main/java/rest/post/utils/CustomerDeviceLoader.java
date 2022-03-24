package rest.post.utils;

import java.io.*;
import java.util.ArrayList;

public class CustomerDeviceLoader {
    private File file;

    public CustomerDeviceLoader(File file) {
        this.file = file;
    }

    public ArrayList<CustomerProfile> getUser() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));

        ArrayList<CustomerProfile> customerList = new ArrayList<>();
        String st;

        // Condition holds true till
        // there is character in a string
        while ((st = br.readLine()) != null) {
            // Print the string
            String[] splitted = st.split(",");
            CustomerProfile customer = new CustomerProfile();
            customer.setCustomerName(splitted[0]);
            customer.setDeviceName(splitted[1]);
//            customer.setAccessToken(splitted[2]);
            customerList.add(customer);
        }
        return customerList;
    }
}

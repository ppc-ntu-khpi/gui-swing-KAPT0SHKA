package com.mybank.domain;

import java.util.ArrayList;
import java.util.List;

public class Bank {
    private static List<Customer> customers = new ArrayList<>();

    public static void addCustomer(String firstName, String lastName) {
        customers.add(new Customer(firstName, lastName));
    }

    public static int getNumberOfCustomers() {
        return customers.size();
    }

    public static Customer getCustomer(int index) {
        return customers.get(index);
    }
}
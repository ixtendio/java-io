package com.example.javaio;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ObjectInputOutputStreamTest {

    @Test
    public void deepClone() throws IOException, ClassNotFoundException {
        //GIVEN
        String firstName = "John";
        String lastName = "Doe";
        String city = "Amsterdam";
        String country = "Netherlands";
        User user = new User(firstName, lastName, new Address(city, country));

        //WHEN
        User clonedUser = deserialize(serialize(user));

        //THEN
        assertNotNull(clonedUser);
        assertNotEquals(clonedUser, user);
        assertEquals(firstName, clonedUser.getFirstName());
        assertEquals(lastName, clonedUser.getLastName());
        assertNotNull(clonedUser.getAddress());
        assertEquals(city, clonedUser.getAddress().getCity());
        assertEquals(country, clonedUser.getAddress().getCountry());
    }

    private static <T extends Serializable> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T) ois.readObject();
        }
    }

    private static <T extends Serializable> byte[] serialize(T object) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ObjectOutputStream oOut = new ObjectOutputStream(out)) {
            oOut.writeObject(object);
            oOut.flush();
            return out.toByteArray();
        }
    }
}

package com.granado.java.serializable;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class JDKSerializer implements SerializeStrategy {


    public JDKSerializer() {
    }

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}

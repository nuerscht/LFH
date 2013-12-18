package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * Simple session serializer to serialize object to play session cookie
 *
 * @author dal
 */
public class SessionSerializer {
    /**
     * Deserialize a byte array to the specific generic type.
     *
     * @param data A byte array with the base64 serialized data
     * @return Returns the generic type <b>T</b>
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] data) {
        try {
            byte[] b = Base64.decodeBase64(data);
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            return (T) si.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * Serialize the generic type <b>T</b> to a base64 string.
     *
     * @param object An instance of <b>T</b>
     * @return A base64 String representation of the instance of <b>T</b>
     */
    public static <T> String serialize(T object) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bo);
            out.writeObject(object);
            out.flush();
            return Base64.encodeBase64String(bo.toByteArray());
        } catch (IOException e) {
            //this exception should never occur
            System.out.println(e);
        }
        return null;
    }

}

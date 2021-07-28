/*
 * www.openamf.org
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package com.weibo.live.rtmp.amf;

import com.weibo.live.rtmp.amf.messaging.io.ASObject;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * AMF Serializer
 *
 * @author Jason Calabrese <jasonc@missionvi.com>
 * @author Pat Maddox <pergesu@users.sourceforge.net>
 * @author Sylwester Lachiewicz <lachiewicz@plusnet.pl>
 * @author Richard Pitt
 * @version $Revision: 1.54 $, $Date: 2006/03/25 23:41:41 $
 */
@Slf4j
public class AMF0Serializer {


    private static final int MILLS_PER_HOUR = 60000;

    /**
     * The output stream
     */
    protected final ByteBuf byteBuf;


    public AMF0Serializer(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }


    public void encodePayload(final ByteBuf out, final List<Object> values) {
        for (final Object value : values) {
            try {
                writeData(value);
            } catch (IOException e) {
                log.error("encodePayload error={}", e);
            }
        }
    }

    /**
     * Writes Data
     *
     * @param value
     * @throws java.io.IOException
     */
    public void writeData(Object value) throws IOException {
        if (value == null) {
            // write null object
            byteBuf.writeByte(AMF0Body.DATA_TYPE_NULL);
        } else if (value instanceof AMF3Object) {
            //writeAMF3Data((AMF3Object) value);
        } else if (isPrimitiveArray(value)) {
            writePrimitiveArray(value);
        } else if (value instanceof Number) {
            // write number object
            byteBuf.writeByte(AMF0Body.DATA_TYPE_NUMBER);
            byteBuf.writeDouble(((Number) value).doubleValue());
        } else if (value instanceof String) {
            byteBuf.writeByte(AMF0Body.DATA_TYPE_STRING);
            encodeString((String) value);
        } else if (value instanceof Character) {
            // write String object
            byteBuf.writeByte(AMF0Body.DATA_TYPE_STRING);
            encodeString(value.toString());
        } else if (value instanceof Boolean) {
            // write boolean object
            byteBuf.writeByte(AMF0Body.DATA_TYPE_BOOLEAN);
            byteBuf.writeBoolean(((Boolean) value).booleanValue());
        } else if (value instanceof Date) {
            // write Date object
            byteBuf.writeByte(AMF0Body.DATA_TYPE_DATE);
            byteBuf.writeDouble(((Date) value).getTime());
            int offset = TimeZone.getDefault().getRawOffset();
            byteBuf.writeShort(offset / MILLS_PER_HOUR);
        } else if (value instanceof ASObject) {
            byteBuf.writeByte(AMF0Body.DATA_TYPE_OBJECT);
            writeMap((Map<?, ?>) value);
        } else {
                /*
                MM's gateway requires all objects to be marked with the
                Serializable interface in order to be serialized
                That should still be followed if possible, but there is
                no good reason to enforce it.
                */
            writeObject(value);
        }
    }


    private void encodeString(final String value) {
        final byte[] bytes = value.getBytes(); // UTF-8 ?
        byteBuf.writeShort((short) bytes.length);
        byteBuf.writeBytes(bytes);
    }

    /**
     * Writes Object
     *
     * @param object
     * @throws java.io.IOException
     */
    protected void writeObject(Object object) throws IOException {
        if (log.isDebugEnabled()) {
            if (object == null) {
                log.debug("Writing object, object param == null");
            } else {
                log.debug("Writing object, class = " + object.getClass());
            }
        }
        //String customClassName = null;
        /*
            OpenAMFConfig.getInstance().getCustomClassName(
                object.getClass().getName());
        */
        //if (customClassName == null) {
        byteBuf.writeByte(AMF0Body.DATA_TYPE_OBJECT);
        /*} else {
            if (log.isDebugEnabled()) {
                log.debug("customClassName : " + customClassName);
            }
            outputStream.writeByte(AMF0Body.DATA_TYPE_CUSTOM_CLASS);
            outputStream.writeUTF(customClassName);
        }*/
        try {
            PropertyDescriptor[] properties =
                    PropertyUtils.getPropertyDescriptors(object);
            for (int i = 0; i < properties.length; i++) {
                if (!properties[i].getName().equals("class")) {
                    String propertyName = properties[i].getName();
                    Method readMethod = properties[i].getReadMethod();
                    Object propertyValue = null;
                    if (readMethod == null) {
                        log.error(
                                "unable to find readMethod for : "
                                        + propertyName
                                        + " writing null!");
                    } else {
                        log.debug("invoking readMethod " + readMethod);
                        propertyValue =
                                readMethod.invoke(object, new Object[0]);
                    }
                    log.debug(propertyName + " = " + propertyValue);
                    encodeString(propertyName);
                    writeData(propertyValue);
                }
            }
            byteBuf.writeShort(0);
            byteBuf.writeByte(AMF0Body.DATA_TYPE_OBJECT_END);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("error={}", e);
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Writes Array Object - call <code>writeData</code> foreach element
     *
     * @param array
     * @throws java.io.IOException
     */
    protected void writeArray(Object[] array) throws IOException {
        byteBuf.writeByte(AMF0Body.DATA_TYPE_ARRAY);
        byteBuf.writeInt(array.length);
        for (int i = 0; i < array.length; i++) {
            writeData(array[i]);
        }
    }

    protected void writePrimitiveArray(Object array) throws IOException {
        writeArray(convertPrimitiveArrayToObjectArray(array));
    }

    protected Object[] convertPrimitiveArrayToObjectArray(Object array) {
        Class<?> componentType = array.getClass().getComponentType();

        Object[] result = null;

        if (componentType == null) {
            throw new NullPointerException("componentType is null");
        } else if (componentType == Character.TYPE) {
            char[] carray = (char[]) array;
            result = new Object[carray.length];
            for (int i = 0; i < carray.length; i++) {
                result[i] = new Character(carray[i]);
            }
        } else if (componentType == Byte.TYPE) {
            byte[] barray = (byte[]) array;
            result = new Object[barray.length];
            for (int i = 0; i < barray.length; i++) {
                result[i] = new Byte(barray[i]);
            }
        } else if (componentType == Short.TYPE) {
            short[] sarray = (short[]) array;
            result = new Object[sarray.length];
            for (int i = 0; i < sarray.length; i++) {
                result[i] = new Short(sarray[i]);
            }
        } else if (componentType == Integer.TYPE) {
            int[] iarray = (int[]) array;
            result = new Object[iarray.length];
            for (int i = 0; i < iarray.length; i++) {
                result[i] = Integer.valueOf(iarray[i]);
            }
        } else if (componentType == Long.TYPE) {
            long[] larray = (long[]) array;
            result = new Object[larray.length];
            for (int i = 0; i < larray.length; i++) {
                result[i] = new Long(larray[i]);
            }
        } else if (componentType == Double.TYPE) {
            double[] darray = (double[]) array;
            result = new Object[darray.length];
            for (int i = 0; i < darray.length; i++) {
                result[i] = new Double(darray[i]);
            }
        } else if (componentType == Float.TYPE) {
            float[] farray = (float[]) array;
            result = new Object[farray.length];
            for (int i = 0; i < farray.length; i++) {
                result[i] = new Float(farray[i]);
            }
        } else if (componentType == Boolean.TYPE) {
            boolean[] barray = (boolean[]) array;
            result = new Object[barray.length];
            for (int i = 0; i < barray.length; i++) {
                result[i] = new Boolean(barray[i]);
            }
        } else {
            throw new IllegalArgumentException(
                    "unexpected component type: "
                            + componentType.getClass().getName());
        }

        return result;
    }

    /**
     * Writes Iterator - convert to List and call <code>writeCollection</code>
     *
     * @param iterator Iterator
     * @throws java.io.IOException
     */
    protected void write(Iterator<?> iterator) throws IOException {
        List<Object> list = new ArrayList<Object>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        write(list);
    }

    /**
     * Writes collection
     *
     * @param collection Collection
     * @throws java.io.IOException
     */
    protected void write(Collection<?> collection) throws IOException {
        byteBuf.writeByte(AMF0Body.DATA_TYPE_ARRAY);
        byteBuf.writeInt(collection.size());
        for (Iterator<?> objects = collection.iterator(); objects.hasNext(); ) {
            Object object = objects.next();
            writeData(object);
        }
    }

    /**
     * Writes Object Map
     *
     * @param map
     * @throws java.io.IOException
     */
    protected void writeMap(Map<?, ?> map) throws IOException {
        /*if (map instanceof ASObject && ((ASObject) map).getType() != null) {
            if (log.isDebugEnabled())
                log.debug(
                        "Writing Custom Class: " + ((ASObject) map).getType());
            byteBuf.writeByte(AMF0Body.DATA_TYPE_CUSTOM_CLASS);
            encodeString(((ASObject) map).getType());
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Writing Map");
            }
            byteBuf.writeByte(AMF0Body.DATA_TYPE_MIXED_ARRAY);
            byteBuf.writeInt(0);
        }*/
        for (Iterator<?> entrys = map.entrySet().iterator(); entrys.hasNext(); ) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entrys.next();
            log.debug(entry.getKey() + ": " + entry.getValue());
            encodeString(entry.getKey().toString());
            writeData(entry.getValue());
        }

        byteBuf.writeShort(0);
        byteBuf.writeByte(AMF0Body.DATA_TYPE_OBJECT_END);

    }

    /**
     * Writes XML Document
     *
     * @param
     * @throws java.io.IOException
     */
    /*protected void write(Document document) throws IOException {
        byteBuf.writeByte(AMF0Body.DATA_TYPE_XML);
        Element docElement = document.getDocumentElement();
        String xmlData = convertDOMToString(docElement);
        if (log.isDebugEnabled())
            log.debug("Writing xmlData: \n" + xmlData);
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        baOutputStream.write(xmlData.getBytes("UTF-8"));
        byteBuf.writeInt(baOutputStream.size());
        baOutputStream.writeTo(outputStream);
    }*/
    protected boolean isPrimitiveArray(Object obj) {
        if (obj == null)
            return false;
        return obj.getClass().isArray() && obj.getClass().getComponentType().isPrimitive();
    }

   /* private void writeAMF3Data(AMF3Object data) throws IOException {
        outputStream.writeByte(AMF0Body.DATA_TYPE_AMF3_OBJECT);
        //ObjectOutput amf3 = GraniteContext.getCurrentInstance().getGraniteConfig().newAMF3Serializer(outputStream);
        ObjectOutput amf3 = new AMF3Serializer(outputStream);
        amf3.writeObject(data.getValue());
    }*/

    public static String convertDOMToString(Node node) {
        StringBuffer sb = new StringBuffer();
        if (node.getNodeType() == Node.TEXT_NODE) {
            sb.append(node.getNodeValue());
        } else {
            String currentTag = node.getNodeName();
            sb.append('<');
            sb.append(currentTag);
            appendAttributes(node, sb);
            sb.append('>');
            if (node.getNodeValue() != null) {
                sb.append(node.getNodeValue());
            }

            appendChildren(node, sb);

            appendEndTag(sb, currentTag);
        }
        return sb.toString();
    }

    private static void appendAttributes(Node node, StringBuffer sb) {
        if (node instanceof Element) {
            NamedNodeMap nodeMap = node.getAttributes();
            for (int i = 0; i < nodeMap.getLength(); i++) {
                sb.append(' ');
                sb.append(nodeMap.item(i).getNodeName());
                sb.append('=');
                sb.append('"');
                sb.append(nodeMap.item(i).getNodeValue());
                sb.append('"');
            }
        }
    }

    private static void appendChildren(Node node, StringBuffer sb) {
        if (node.hasChildNodes()) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                sb.append(convertDOMToString(children.item(i)));
            }
        }
    }

    private static void appendEndTag(StringBuffer sb, String currentTag) {
        sb.append('<');
        sb.append('/');
        sb.append(currentTag);
        sb.append('>');
    }
}
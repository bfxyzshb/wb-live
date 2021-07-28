/*
 * www.openamf.org
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package com.weibo.live.rtmp.amf;


import com.weibo.live.rtmp.amf.messaging.io.ASObject;
import com.weibo.live.rtmp.constants.AMF0Type;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.UTFDataFormatException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


/**
 * AMF Deserializer
 *
 * @author Jason Calabrese <jasonc@missionvi.com>
 * @author Pat Maddox <pergesu@users.sourceforge.net>
 * @author Sylwester Lachiewicz <lachiewicz@plusnet.pl>
 * @version $Revision: 56 $, $Date: 2007-11-05 12:19:46 +0200 (Mon, 05 Nov 2007) $
 */
@Slf4j
public final class AMF0Deserializer {

    private final ByteBuf byteBuf;

    public AMF0Deserializer(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    private List<Object> storedObjects = new ArrayList<>();

    public List<Object> decodeAmfPayload() {
        List<Object> list = new ArrayList<>();
        try {
            //System.out.println(des.readData(payload, payload.readByte()));
            while (byteBuf.isReadable()) {
                byte type = byteBuf.readByte();
                Object decode = readData(type);
                list.add(decode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    ASObject readObject(ASObject aso) throws IOException {
        storeObject(aso);
        // Init the array
        if (log.isDebugEnabled()) log.debug("reading object");
        // Grab the key
        String key = readString(AMF0Type.TYPE_STRING);
        for (byte type = byteBuf.readByte(); type != 9; type = byteBuf.readByte()) {
            // Grab the value
            Object value = readData(type);
            // Save the name/value pair in the map
            if (value == null) {
                log.info("Skipping NULL value for :" + key);
            } else {
                aso.put(key, value);
                if (log.isDebugEnabled())
                    log.debug(" adding {key=" + key + ", value=" + value + ", type=" + type + "}");
            }
            // Get the next name
            key = readString(AMF0Type.TYPE_STRING);
        }
        if (log.isDebugEnabled()) log.debug("finished reading object");
        // Return the map
        return aso;
    }

    /**
     * Reads array
     *
     * @return
     * @throws IOException
     */
    protected List<?> readArray() throws IOException {
        // Init the array
        List<Object> array = new ArrayList<Object>();
        storeObject(array);
        if (log.isDebugEnabled()) log.debug("Reading array");
        // Grab the length of the array
        long length = byteBuf.readInt();
        if (log.isDebugEnabled()) log.debug("array length = " + length);
        // Loop over all the elements in the data
        for (long i = 0; i < length; i++) {
            // Grab the type for each element
            byte type = byteBuf.readByte();
            // Grab the element
            Object data = readData(type);
            array.add(data);
        }
        // Return the data
        return array;
    }

    /**
     * Store object in  internal array
     *
     * @param o
     */
    private void storeObject(Object o) {
        storedObjects.add(o);
        if (log.isDebugEnabled()) log.debug("storedObjects.size: " + storedObjects.size());
    }

    /**
     * Reads date
     *
     * @return
     * @throws IOException
     */
    protected Date readDate() throws IOException {
        long ms = (long) byteBuf.readDouble(); // Date in millis from 01/01/1970

        // here we have to read in the raw
        // timezone offset (which comes in minutes, but incorrectly signed),
        // make it millis, and fix the sign.
        int timeoffset = byteBuf.readShort() * 60000 * -1; // now we have millis

        TimeZone serverTimeZone = TimeZone.getDefault();

        // now we subtract the current timezone offset and add the one that was passed
        // in (which is of the Flash client), which gives us the appropriate ms (i think)
        // -alon
        Calendar sent = new GregorianCalendar();
        sent.setTime((new Date(ms - serverTimeZone.getRawOffset() + timeoffset)));

        TimeZone sentTimeZone = sent.getTimeZone();

        // we have to handle daylight savings ms as well
        if (sentTimeZone.inDaylightTime(sent.getTime())) {
            //
            // Implementation note: we are trying to maintain compatibility
            // with J2SE 1.3.1
            //
            // As such, we can't use java.util.Calendar.getDSTSavings() here
            //
            sent.setTime(new Date(sent.getTime().getTime() - 3600000));
        }

        return sent.getTime();
    }

    /**
     * Reads flushed stored object
     *
     * @return
     * @throws IOException
     */
    protected Object readFlushedSO() throws IOException {
        int index = byteBuf.readUnsignedShort();
        if (log.isDebugEnabled()) log.debug("Object Index: " + index);
        return storedObjects.get(index);
    }

    /**
     * Reads object
     *
     * @return
     */
    protected Object readASObject() {
        return null;
    }

    /**
     * Reads object
     *
     * @return
     */
    protected Object readAMF3Data() throws IOException {
       /* ObjectInput amf3 = new AMF3Deserializer(inputStream);

        try {
            return amf3.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/
        return null;
    }

    protected ASObject readObject() throws IOException {
        ASObject aso = new ASObject();
        return readObject(aso);
    }

    public Object readData(byte type) throws IOException {
        if (log.isDebugEnabled()) log.debug("Reading data of type " + AMF0Body.getObjectTypeDescription(type));
        switch (type) {
            case AMF0Body.DATA_TYPE_NUMBER: // 0
                return new Double(byteBuf.readDouble());
            case AMF0Body.DATA_TYPE_BOOLEAN: // 1
                return Boolean.valueOf(byteBuf.readBoolean());
            case AMF0Body.DATA_TYPE_STRING: // 2
                return readString(AMF0Body.DATA_TYPE_STRING);
            case AMF0Body.DATA_TYPE_OBJECT: // 3
                return readObject();
            case AMF0Body.DATA_TYPE_MOVIE_CLIP: // 4
                throw new IOException("Unknown/unsupported object type " + AMF0Body.getObjectTypeDescription(type));
            case AMF0Body.DATA_TYPE_NULL: // 5
            case AMF0Body.DATA_TYPE_UNDEFINED: //6
                return null;
            case AMF0Body.DATA_TYPE_REFERENCE_OBJECT: // 7
                return readFlushedSO();
            case AMF0Body.DATA_TYPE_MIXED_ARRAY: // 8
                /*long length =*/
                byteBuf.readInt();
                //don't do anything with the length
                return readObject();
            case AMF0Body.DATA_TYPE_OBJECT_END: // 9
                return null;
            case AMF0Body.DATA_TYPE_ARRAY: // 10
                return readArray();
            case AMF0Body.DATA_TYPE_DATE: // 11
                return readDate();
            case AMF0Body.DATA_TYPE_LONG_STRING: // 12
                return readString(AMF0Body.DATA_TYPE_LONG_STRING);
            case AMF0Body.DATA_TYPE_AS_OBJECT: // 13
                return readASObject();
            case AMF0Body.DATA_TYPE_RECORDSET: // 14
                return null;
            case AMF0Body.DATA_TYPE_XML: // 15
                //return convertToDOM(inputStream);
                return null;
            case AMF0Body.DATA_TYPE_CUSTOM_CLASS: // 16
                //return readCustomClass();
                return null;
            case AMF0Body.DATA_TYPE_AMF3_OBJECT: // 17
                return readAMF3Data();
            default:
                throw new IOException("Unknown/unsupported object type " + AMF0Body.getObjectTypeDescription(type));
        }
    }

    public String readString(byte type) {
        int len = 0;
        switch (type) {
            case AMF0Body.DATA_TYPE_LONG_STRING:
                len = byteBuf.readInt();
                break;
            case AMF0Body.DATA_TYPE_STRING:
                len = byteBuf.readShort() & 0xffff; //buf.getUnsignedShort();
                break;
            default:
                log.debug("Unknown AMF type: {}", type);
        }
        return bufferToString(byteBuf, len);
    }

    private final static String bufferToString(final ByteBuf byteBuf, int len) {
        //Trac #601 - part of the problem seems to be a null byte buffer
        String str = null;
        if (byteBuf != null) {
            byte[] bytes = new byte[len];
            byteBuf.readBytes(bytes);
            str = new String(bytes, AMF0Type.CHARSET);
            log.debug("str: {}", str);
        } else {
            log.warn("ByteBuffer was null attempting to read String");
        }
        return str;
    }


    /**
     * This is a hacked verison of Java's DataInputStream.readUTF(), which only
     * supports Strings <= 65535 UTF-8-encoded characters
     */
    private Object readLongUTF(DataInputStream in) throws IOException {
        int utflen = in.readInt();
        StringBuffer str = new StringBuffer(utflen);
        byte bytearr[] = new byte[utflen];
        int c, char2, char3;
        int count = 0;

        in.readFully(bytearr, 0, utflen);

        while (count < utflen) {
            c = bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    /* 0xxxxxxx*/
                    count++;
                    str.append((char) c);
                    break;
                case 12:
                case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        throw new UTFDataFormatException();
                    char2 = bytearr[count - 1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new UTFDataFormatException();
                    str.append((char) (((c & 0x1F) << 6) | (char2 & 0x3F)));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen)
                        throw new UTFDataFormatException();
                    char2 = bytearr[count - 2];
                    char3 = bytearr[count - 1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException();
                    str.append((char) (((c & 0x0F) << 12) |
                            ((char2 & 0x3F) << 6) |
                            ((char3 & 0x3F) << 0)));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    throw new UTFDataFormatException();
            }
        }

        // The number of chars produced may be less than utflen
        return new String(str);
    }

    /*public static Document convertToDOM(InputStream is) throws IOException {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        is.skip(4); // skip length
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(is));
        } catch (Exception e) {
            //log.error(e, e);
            throw new IOException("Error while parsing xml: " + e.getMessage());
        }
        return document;
    }*/
}
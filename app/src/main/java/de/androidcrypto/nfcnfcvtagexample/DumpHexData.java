package de.androidcrypto.nfcnfcvtagexample;

import java.util.Arrays;

/**
 * the class dumps hex data to a more structured format and appends the ASCII representation of the data
 */

public class DumpHexData {
    public static byte[] contentLoaded; // needs to be filled before running the chunking


    public static String hexPrint(int address, byte[] data) {
        // get hex address of part
        String hexAddress = formatWithNullsLeft(Integer.toHexString(address), 8) + ":";
        String hexContent = bytesToHexBlank(data);
        // add blanks depending on data length (7 = add 3 blanks, 6 = add 6 blanks
        for (int i = 0; i < (8 - data.length); i++) {
            hexContent += "   ";
        }
        String asciiRowString = "";
        for (int j = 0; j < data.length; j++) {
            // check for maximal characters
            asciiRowString = asciiRowString + returnPrintableChar(data[j], true);
        }
        String hexAscii = (char) 124 + formatWithBlanksRight(asciiRowString, 8);
        return hexAddress + hexContent + hexAscii;
    }

    private static String formatWithNullsLeft(String value, int len) {
        while (value.length() < len) {
            value = "0" + value;
        }
        return value;
    }

    private static String formatWithBlanksRight(String value, int len) {
        while (value.length() < len) {
            value += " ";
        }
        return value;
    }

    private static String bytesToHexBlank(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte b : bytes)
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1)).append(" ");
        return result.toString();
    }

    private static char returnPrintableChar(byte inputByte, Boolean printDotBool) {
        // ascii-chars from these ranges are printed
        // 48 -  57 = 0-9
        // 65 -  90 = A-Z
        // 97 - 122 = a-z
        // if printDotBool = true then print a dot "."
        char returnChar = 0;
        if (printDotBool == true) {
            returnChar = 46;
        }
        if ((inputByte >= 48) && (inputByte <= 57)) {
            returnChar = (char) inputByte;
        }
        if ((inputByte >= 65) && (inputByte <= 90)) {
            returnChar = (char) inputByte;
        }
        if ((inputByte >= 97) && (inputByte <= 122)) {
            returnChar = (char) inputByte;
        }
        return returnChar;
    }

    public static byte[] get8ByteChunk(int chunkPart) {
        // needs a global defined contentLoaded byte array
        return Arrays.copyOfRange(contentLoaded, (chunkPart * 8), ((chunkPart * 8) + 8));
    }

    public static byte[] getLastByteChunk(int chunkPart, int numberLastBytes) {
        // needs a global defined contentLoaded byte array
        return Arrays.copyOfRange(contentLoaded, (chunkPart * 8), ((chunkPart * 8) + numberLastBytes));
    }

}

package de.androidcrypto.nfcmifareultralightexample;

import static de.androidcrypto.nfcmifareultralightexample.Utils.printData;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.util.Log;

import java.io.IOException;

/**
 * this class takes a TAG and tries to identify the type and subtype of the NFC tag, along with some useful information
 */

public class TagIdentification {
    private final String TAG = "TagIdentification";

    private Tag tag; // the tag
    private Enum preferredTech;
    // internal classes, usage depending on techList
    private MifareClassic mfc;
    private MifareUltralight mfu;
    private NfcA nfcA;
    private NfcB nfcB;
    private NfcF nfcF;
    private NfcV nfcV;
    private Ndef ndef;
    private NdefFormatable ndefFormatable;
    private IsoDep isoDep;
    private int sizeOfTechList = 0;
    private boolean isMifareClassic = false;
    private boolean isMifareUltralight = false;
    private boolean isNfca = false;
    private boolean isNdefFormatable = false;

    private boolean isIsoDep = false;
    private boolean isUnknownTape = false;

    // success in reading the technology
    private boolean mifareUltralightSuccess = false;

    private String[] techList; // get it from the tag
    private String tagId; // get it from the tag
    private int tagType; // get it from the tag
    // following values are analyzed
    private boolean isTested = false;
    private String tagTypeName;
    private int tagTypeSub;
    private String tagTypeSubName;
    private int tagSizeInBytes = 0;
    private int tagSizeUserInBytes = 0;
    private int numberOfCounters = 0;
    private int numberOfPages = 0;
    private int numberOfPageStartUserMemory = 0;


    public TagIdentification(Tag tag) {
        Log.d(TAG, "Tag identification started");
        this.tag = tag;
        doIdentification();
    }

    public TagIdentification(Tag tag, Enum preferredTech) {
        Log.d(TAG, "Tag identification started");
        this.tag = tag;
        this.preferredTech = preferredTech;
        doIdentification();
    }

    public enum tech {
        MifareClassic,
        MifareUltralight
    }

    private void doIdentification() {
        // step 1 - what classes can we use to access the tag
        techList = mfu.getTag().getTechList();
        identifyClasses();
        // step 2 is depending on techList technologies

        // todo start with preferredTech from Enum preferredTech

        if (isMifareUltralight) {
            Log.d(TAG, "Analyze of Mifare Ultralight started");
            analyzeMifareUltralight();
        }


    };

    private void identifyClasses() {
        Log.d(TAG, "Tag identification of classes started");
        sizeOfTechList = techList.length;
        for (int i = 0; i < sizeOfTechList; i++) {
            boolean entryFound = false;
            String techListEntry = techList[i];
            if (techListEntry.equals("android.nfc.tech.MifareClassic")) {
                isMifareClassic = true;
                entryFound = true;
            } else if (techListEntry.equals("android.nfc.tech.MifareUltralight")) {
                isMifareUltralight = true;
                entryFound = true;
            } else if (techListEntry.equals("android.nfc.tech.NfcA")) {
                isNfca = true;
                entryFound = true;
            } else if (techListEntry.equals("android.nfc.tech.NdefFormatable")) {
                isNdefFormatable = true;
                entryFound = true;
            }
            if (!entryFound) {
                isUnknownTape = true;
            }
        } // for (int i = 0; i < sizeOfTechList; i++) {
    }

    /**
     * section for Mifare Classic methods
     */


    /**
     * section for Mifare Ultralight methods
     */

    public enum techUltralight {
        MifareUltralightFirst,
        MifareUltralightC,
        MifareUltralightEv1
    }

    private void analyzeMifareUltralight() {
        mfu = MifareUltralight.get(tag);
        if (mfu == null) {
            mifareUltralightSuccess = false;
            return;
        }
        tagTypeName = "Mifare Ultralight";
        tagId = bytesToHexNpe(mfu.getTag().getId());

        boolean isUltralight = false;
        boolean isUltralightC = false;
        boolean isUltralightEv1 = false;

        // checks for distinguishing the correct type of card
        byte[] getVersionResp = mifareUltralightGetVersion(mfu);
        byte[] doAuthResp = mifareUltralightDoAuthenticate(mfu);
        // if getVersionResponse is not null it is an Ultralight EV1 or later
        if (getVersionResp != null) {
            isUltralightEv1 = true;
            // storage size is in byte 6, 0b or 0e
            if (getVersionResp[6] == (byte) 0x0b) {
                tagSizeInBytes = 64; // complete memory
                tagSizeUserInBytes = 48; // user memory
                numberOfPages = tagSizeInBytes / 4;
            }
            if (getVersionResp[6] == (byte) 0x0e) {
                tagSizeInBytes = 144; // complete memory
                tagSizeUserInBytes = 128; // user memory
                numberOfPages = tagSizeInBytes / 4;
            }

        } else {
            // now we are checking if getVersionResponse is not null meaning an Ultralight-C tag
            if (doAuthResp != null) {
                isUltralightC = true;
                tagSizeInBytes = 192; // complete memory
                tagSizeUserInBytes = 144; // user memory
                numberOfPages = tagSizeInBytes / 4;

            } else {
                // the tag is an Ultralight tag
                isUltralight = true;
                tagSizeInBytes = 64; // complete memory
                tagSizeUserInBytes = 48; // user memory
                numberOfPages = tagSizeInBytes / 4;
            }
        }
        // tag identification
        if (isUltralight) {
            Log.d(TAG, "Tag is a Mifare Ultralight with a storage size of " + tagSizeInBytes + " bytes");
            tagTypeSubName = techUltralight.MifareUltralightFirst.toString();
            tagTypeSub = 0;
        }
        if (isUltralightC) {
            Log.d(TAG, "Tag is a Mifare Ultralight-C with a storage size of " + tagSizeInBytes + " bytes");
            tagTypeSubName = techUltralight.MifareUltralightC.toString();
            tagTypeSub = 1;
        }
        if (isUltralightEv1) {
            Log.d(TAG, "Tag is a Mifare Ultralight EV1 with a storage size of " + tagSizeInBytes + " bytes");
            tagTypeSubName = techUltralight.MifareUltralightEv1.toString();
            tagTypeSub = 2;
        }



    }

    private byte[] mifareUltralightGetVersion(MifareUltralight mfu) {
        byte[] getVersionResponse = null;
        try {
            byte[] getVersionCommand = new byte[]{(byte) 0x60};
            getVersionResponse = mfu.transceive(getVersionCommand);
            return getVersionResponse;
        } catch (IOException e) {
            Log.d(TAG, "Mifare Ultralight getVersion unsupported, IOException: " + e.getMessage());
        }
        // this is just an advice - if an error occurs - close the connection and reconnect the tag
        // https://stackoverflow.com/a/37047375/8166854
        try {
            mfu.close();
        } catch (Exception e) {
        }
        try {
            mfu.connect();
        } catch (Exception e) {
        }
        return null;
    }

    // just for distinguish between Ultralight (auth fails) and Ultralight C (auth succeeds)
    private byte[] mifareUltralightDoAuthenticate(MifareUltralight mfu) {
        byte[] getAuthresponse = null;
        try {
            byte[] getAuthCommand = new byte[]{(byte) 0x1a, (byte) 0x00};
            getAuthresponse = mfu.transceive(getAuthCommand);
            return getAuthresponse;
        } catch (IOException e) {
            Log.d(TAG, "Mifare Ultralight doAuthentication unsupported, IOException: " + e.getMessage());
        }
        // this is just an advice - if an error occurs - close the connenction and reconnect the tag
        // https://stackoverflow.com/a/37047375/8166854
        try {
            mfu.close();
        } catch (Exception e) {
        }
        try {
            mfu.connect();
        } catch (Exception e) {
        }
        return null;
    }


    /**
     * section for
     */




    private static String bytesToHexNpe(byte[] bytes) {
        if (bytes == null) return "";
        StringBuffer result = new StringBuffer();
        for (byte b : bytes)
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

}
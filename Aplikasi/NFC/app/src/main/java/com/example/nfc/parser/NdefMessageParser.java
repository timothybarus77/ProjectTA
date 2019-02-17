package com.example.nfc.parser;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.example.nfc.record.ParseNDEF;
import com.example.nfc.record.SmartPoster;
import com.example.nfc.record.TextRecord;
import com.example.nfc.record.URIRecord;

import java.util.ArrayList;
import java.util.List;

public class NdefMessageParser {

    private NdefMessageParser() {
    }

    public static List<ParseNDEF> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    public static List<ParseNDEF> getRecords(NdefRecord[] records) {
        List<ParseNDEF> elements = new ArrayList<ParseNDEF>();

        for (final NdefRecord record : records) {
            if (URIRecord.isUri(record)) {
                elements.add(URIRecord.parse(record));
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            } else if (SmartPoster.isPoster(record)) {
                elements.add(SmartPoster.parse(record));
            } else {
                elements.add(new ParseNDEF() {
                    public String str() {
                        return new String(record.getPayload());
                    }
                });
            }
        }

        return elements;
    }
}
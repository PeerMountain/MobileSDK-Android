package com.peermountain.core.persistence;

import com.peermountain.core.model.guarded.AppDocument;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Galeen on 10/26/2017.
 */

public class DaoDocument {
    static void saveDocument(AppDocument document) {
        if (SharedPreferenceManager.getContext() == null) return;
        try {
            SharedPreferenceManager.putString(document.getId(), MyJsonParser.writeAppDocument(document));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static AppDocument getDocument(String id) {
        if (SharedPreferenceManager.getContext() == null) return null;
        try {
            return MyJsonParser.readAppDocument(SharedPreferenceManager.getString(id, null));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    static void addDocument(AppDocument document) {
        saveDocument(document);
        addIdToDocuments(document.getId());
    }
    static void saveDocuments(ArrayList<AppDocument> documents) {
        if (SharedPreferenceManager.getContext() == null) return;
        if (documents == null) {
            removeDocuments();
            return;
        }
        ArrayList<String> keys = new ArrayList<>();
//        for (AppDocument document : documents) {
//            keys.add(document.getId());
//            saveDocument(document);
//        }
        for (int i = 0; i < documents.size(); i++) {
            AppDocument document = documents.get(i);
            keys.add(document.getId());
            saveDocument(document);
        }
        StringBuilder sb = new StringBuilder();
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            String key = keys.get(i);
            sb.append(key);
            if (i < size - 1) sb.append(",");
        }

        SharedPreferenceManager.putString(SharedPreferenceManager.PREF_MY_DOCUMENTS, sb.toString());
    }

    private static void removeDocuments() {
        String[] idsArr = getDocumentIds();
        if (idsArr != null) {
            for (String id : idsArr) {
                SharedPreferenceManager.putString(id, null);
            }
        }
        SharedPreferenceManager.putString(SharedPreferenceManager.PREF_MY_DOCUMENTS, null);
    }

    private static void addIdToDocuments(String id) {
        String[] idsArr = getDocumentIds();
        String[] ids = new String[1];
        if (idsArr != null) {
            ids = new String[idsArr.length+1];
            for (int i = 0; i < idsArr.length; i++) {
               ids[i] = idsArr[i];
            }
        }
        ids[ids.length-1] = id;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
            sb.append(ids[i]);
            if (i < ids.length - 1) sb.append(",");
        }
        SharedPreferenceManager.putString(SharedPreferenceManager.PREF_MY_DOCUMENTS, sb.toString());
    }

    static void removeDocument(String idToRemove) {
        String[] idsArr = getDocumentIds();
        if (idsArr != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < idsArr.length; i++) {
                String id = idsArr[i];
                if (id.equals(idToRemove)) {
                    SharedPreferenceManager.putString(id, null);//remove document
                } else {
                    sb.append(id).append(",");
                }
            }
            if(sb.length()>0){
                sb.deleteCharAt(sb.length()-1);//remove last ,
            }
            SharedPreferenceManager.putString(SharedPreferenceManager.PREF_MY_DOCUMENTS, sb.toString());//update ids
        }
    }

    static ArrayList<AppDocument> getDocuments() {
        if (SharedPreferenceManager.getContext() == null) return null;
        ArrayList<AppDocument> documents = new ArrayList<>();
        String[] idsArr = getDocumentIds();
        if (idsArr != null) {
            int size = idsArr.length;
            for (int i = 0; i < size; i++) {
                AppDocument document = getDocument(idsArr[i]);
                if (document != null) {
                    documents.add(document);
                }
            }
        }
        return documents;
    }

    private static String[] getDocumentIds() {
        String ids = SharedPreferenceManager.getString(SharedPreferenceManager.PREF_MY_DOCUMENTS, null);
        if (ids != null) {
            return ids.split(",");
        }
        return null;
    }
}

package com.peermountain.core.persistence;

import android.content.Context;
import android.text.TextUtils;

import com.peermountain.core.model.unguarded.Keyword;
import com.peermountain.core.model.unguarded.Keywords;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PeerMountainCoreConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by Galeen on 10/20/2017.
 */

class KeywordsHelper {

    public static Keywords getRandomKeywords(Context context) {
        ArrayList<Keyword> all = new ArrayList<>();
        loadAssetList(context, all, "kycwords.txt");
        HashSet<Keyword> random = new HashSet<>();
        Random rnd = new Random();
        while (random.size() < PeerMountainCoreConstants.KEYWORDS_SHOW_COUNT) {
            random.add(all.get(rnd.nextInt(all.size())));
        }
        return new Keywords(random);
    }

    public static Keywords getRandomKeywordsWithSavedIncluded(Context context) {
        ArrayList<Keyword> all = new ArrayList<>();
        loadAssetList(context, all, "kycwords.txt");
        Keywords savedKeywordsObject = PeerMountainManager.getSavedKeywordsObject();
        Random rnd = new Random();
        HashSet<Keyword> random6Set = new HashSet<>();
        if (savedKeywordsObject != null && savedKeywordsObject.getKeywords() != null) {
            while (random6Set.size() < PeerMountainCoreConstants.MIN_KEYWORDS_SAVE) {
                Keyword keyword = savedKeywordsObject.getKeywords().get(rnd.nextInt(savedKeywordsObject.getKeywords().size()));
                keyword.setSelected(true);
                random6Set.add(keyword);
            }
        }
        HashSet<Keyword> randomAllSet = new HashSet<>(random6Set);
        ArrayList<Keyword> list = new ArrayList<>(random6Set);
        while (randomAllSet.size() < PeerMountainCoreConstants.KEYWORDS_SHOW_TO_VALIDATE_COUNT) {
            Keyword kwr = all.get(rnd.nextInt(all.size()));
            if (!randomAllSet.contains(kwr)) {
                randomAllSet.add(kwr);
                list.add(kwr);
            }
        }
        return new Keywords(list);
    }

    private static void loadAssetList(Context context, ArrayList<Keyword> list, String file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(file), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (!TextUtils.isEmpty(mLine)) {
                    list.add(new Keyword(mLine.trim()));
                }
            }
        } catch (IOException e) {
            LogUtils.e("loadData Keywords", e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LogUtils.e("close loadData Keywords", e.getMessage());
                }
            }
        }
    }
}

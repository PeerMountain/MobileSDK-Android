/*
 * Copyright (C) 2017 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.peermountain.core.odk.utils;

import android.os.Build;
import android.text.Html;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    private static Callback createHeader = new Callback() {
        public String matchFound(MatchResult match) {
            int level = match.group(1).length();
            return "<h" + level + ">" + match.group(2).replaceAll("#+$", "").trim() + "</h" + level
                    + ">";
        }
    };

    private static Callback createParagraph = new Callback() {
        public String matchFound(MatchResult match) {
            String trimmed = match.group(1).trim();
            if (trimmed.matches("(?i)^<\\/?(h|p|bl)")) {
                return match.group(1);
            }
            return "<p>" + trimmed + "</p>";
        }
    };

    private static Callback createSpan = new Callback() {
        public String matchFound(MatchResult match) {
            String attributes = sanitizeAttributes(match.group(1));
            return "<font" + attributes + ">" + match.group(2).trim() + "</font>";
        }

        // throw away all styles except for color and font-family
        private String sanitizeAttributes(String attributes) {

            String stylesText = attributes.replaceAll("style=[\"'](.*?)[\"']", "$1");
            String[] styles = stylesText.trim().split(";");
            StringBuffer stylesOutput = new StringBuffer();

            for (String style : styles) {
                String[] stylesAttributes = style.trim().split(":");
                if (stylesAttributes[0].equals("color")) {
                    stylesOutput.append(" color=\"" + stylesAttributes[1] + "\"");
                }
                if (stylesAttributes[0].equals("font-family")) {
                    stylesOutput.append(" face=\"" + stylesAttributes[1] + "\"");
                }
            }

            return stylesOutput.toString();
        }
    };

    protected static String markdownToHtml(String text) {

        text = text.replaceAll("<([^a-zA-Z/])", "&lt;$1");
        // https://github.com/enketo/enketo-transformer/blob/master/src/markdown.js

        // span - replaced &lt; and &gt; with <>
        text = replace("(?s)<\\s?span([^\\/\n]*)>((?:(?!<\\/).)+)<\\/\\s?span\\s?>",
                text, createSpan);
        // strong
        text = text.replaceAll("(?s)__(.*?)__", "<strong>$1</strong>");
        text = text.replaceAll("(?s)\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        // emphasis
        text = text.replaceAll("(?s)_([^\\s][^_\n]*)_", "<em>$1</em>");
        text = text.replaceAll("(?s)\\*([^\\s][^\\*\n]*)\\*", "<em>$1</em>");
        // links
        text = text.replaceAll("(?s)\\[([^\\]]*)\\]\\(([^\\)]+)\\)",
                "<a href=\"$2\" target=\"_blank\">$1</a>");
        // headers - requires ^ or breaks <font color="#f58a1f">color</font>
        text = replace("(?s)^(#+)([^\n]*)$", text, createHeader);
        // paragraphs
        text = replace("(?s)([^\n]+)\n", text, createParagraph);

        return text;
    }

    public static CharSequence textToHtml(String htmlText) {

        if (htmlText == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= 24) {//markdownToHtml(text)
            return Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(htmlText);
        }
    }

    public interface Callback {
        /**
         * This function is called when a match is made. The string which was matched
         * can be obtained via match.group(), and the individual groupings via
         * match.group(n).
         */
        String matchFound(MatchResult match);
    }

    /**
     * Replaces with callback, with no limit to the number of replacements.
     * Probably what you want most of the time.
     */
    public static String replace(String pattern, String subject, Callback callback) {
        return replace(pattern, subject, -1, null, callback);
    }

    public static String replace(String pattern, String subject, int limit, Callback callback) {
        return replace(pattern, subject, limit, null, callback);
    }

    /**
     * @param regex    The regular expression pattern to search on.
     * @param subject  The string to be replaced.
     * @param limit    The maximum number of replacements to make. A negative value
     *                 indicates replace all.
     * @param count    If this is not null, it will be set to the number of
     *                 replacements made.
     * @param callback Callback function
     */
    public static String replace(String regex, String subject, int limit,
                                 AtomicInteger count, Callback callback) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = Pattern.compile(regex).matcher(subject);
        int i;
        for (i = 0; (limit < 0 || i < limit) && matcher.find(); i++) {
            String replacement = callback.matchFound(matcher.toMatchResult());
            replacement = Matcher.quoteReplacement(replacement); //probably what you want...
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        if (count != null) {
            count.set(i);
        }
        return sb.toString();
    }
} 

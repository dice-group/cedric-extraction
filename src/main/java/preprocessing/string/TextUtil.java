package preprocessing.string;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Based on:
 * {@see https://github.com/dice-group/FOX/blob/master/src/main/java/org/aksw/fox/utils/FoxTextUtil.java}
 *
 * @author Cedric Richter
 */
public class TextUtil {
    public static Logger logger       = LogManager.getLogger(TextUtil.class);
    /**
     * Defines token.
     */
    public static final String tokenSpliter = "[\\p{Punct}&&[^-\\_/&+.]]| |\\t|\\n";

    private TextUtil() {
    }


    /**
     *
     * @param input
     * @return
     */
    public static synchronized String[] getSentencesToken(String input) {

        List<String> result = new ArrayList<>();
        for (String sentence : _getSentences(input))
            result.addAll(new ArrayList<>(Arrays.asList(getSentenceToken(sentence))));

        return result.toArray(new String[result.size()]);
    }

    public static synchronized String[] getSentences(String source) {

        String[] sentences = _getSentences(source);

        // logger.info("sentences: " + sentences.length);

        return sentences;
    }

    /**
     * Gets sentences.
     *
     * @param source
     *            plain text of sentences
     * @return sentences
     */
    protected static synchronized String[] _getSentences(String source) {
        // TODO: use a better one?

        Document doc = new Document(source);

        String[] sentences = null;

        List<Sentence> sentences1 = doc.sentences();

        sentences = new String[sentences1.size()];

        for(int i = 0; i < sentences1.size(); i++){
            sentences[i] = sentences1.get(i).text();
        }


        return sentences;
    }

    /**
     * Gets token of one sentence, token defined by
     *
     * @param sentence
     *            (with punctuation mark)
     * @return token
     */
    public static synchronized String[] getSentenceToken(String sentence) {
        // System.out.println(sentence);
        // Note: Points won't removed, so we remove punctuation marks to points
        // and handle them later
        char punctuationMark = sentence.trim().charAt(sentence.trim().length() - 1);
        if (punctuationMark == '!' || punctuationMark == '?') {
            int punctuationMarkIndex = sentence.lastIndexOf(punctuationMark);
            sentence = sentence.substring(0, punctuationMarkIndex) + "." + sentence.substring(punctuationMarkIndex + 1, sentence.length());
        }

        String[] token = null;
        token = getToken(sentence);

        if (token.length > 0) {
            // remove punctuation mark(points)
            String lastToken = token[token.length - 1];
            if (lastToken.charAt(lastToken.length() - 1) == '.')
                token[token.length - 1] = lastToken.substring(0, lastToken.length() - 1);

            // add a token to keep original length
            int len = sentence.length();

            String cleanSentence = StringUtils.join(token, " ");

            int cleanSentenceLen = cleanSentence.length();

            String closeLen = "";
            while (cleanSentenceLen + closeLen.length() < len) {
                closeLen += " ";
            }
            // add this token
            if (!closeLen.isEmpty())
                token = ArrayUtils.add(token, token.length, closeLen);

            // logger.info("----");
            // logger.info("<" + len + ">");
            // logger.info("<" + cleanSentenceLen + ">");
            // logger.info("<" + sentence + ">");
            // logger.info("<" + cleanSentence + ">");
            // logger.info("<" + StringUtils.join(token, " ") + ">");
            // logger.info("<" + token[token.length - 1] + ">");
        } else {
            token = new String[0];
        }
        return token;
    }

    /**
     * Gets token defined by .
     *
     * @param in
     *            string to split
     * @return token
     */
    public static synchronized String[] getToken(String in) {
        return in.split(tokenSpliter);
    }

    // token needs to bound in spaces e.g.: " Leipzig "
    public static synchronized Set<Integer> getIndices(String token, String tokenInput) {

        Set<Integer> indices = new HashSet<>();
        if (token != null && tokenInput != null && token.length() < tokenInput.length()) {

            token = new StringBuilder().append(" ").append(token.trim()).append(" ").toString();
            tokenInput = new StringBuilder().append(" ").append(tokenInput).append(" ").toString();

            token = Pattern.quote(token);
            Matcher matcher = Pattern.compile(token).matcher(tokenInput);
            while (matcher.find())
                indices.add(matcher.start() + 1 - 1);
        }
        return indices;
    }
}

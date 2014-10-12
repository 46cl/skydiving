package fhacktory.nlp

import opennlp.tools.postag.POSModel
import opennlp.tools.postag.POSTaggerME
import opennlp.tools.tokenize.Tokenizer
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel

/**
 * @version $Id$
 */
class NounsExtractor
{
    Tokenizer tokenizer

    POSTaggerME tagger

    def init()
    {
        TokenizerModel tokenizerModel = new TokenizerModel(this.class.getResourceAsStream("/nlp-models/fr-token.bin"));
        tokenizer = new TokenizerME(tokenizerModel);

        POSModel posModel = new POSModel(this.class.getResourceAsStream("/nlp-models/fr-pos.bin"));
        tagger = new POSTaggerME(posModel);
    }

    List<String> extractNouns(String sentence)
    {
        String[] tokens = tokenizer.tokenize(sentence);
        String[] tags = tagger.tag(tokens);

        // Find common nouns and proper nouns
        def indexes = tags.findIndexValues({ String tag -> ["NC", "NPP"].contains(tag) });
        def result = indexes.collect({ Long index -> tokens[index] }).unique(false)

        result
    }
}

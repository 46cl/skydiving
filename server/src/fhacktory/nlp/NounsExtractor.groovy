package fhacktory.nlp

import opennlp.tools.chunker.ChunkerME
import opennlp.tools.chunker.ChunkerModel
import opennlp.tools.parser.Parser
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
    Parser parser

    Tokenizer tokenizer

    POSTaggerME tagger

    ChunkerME chunker

    def init()
    {
        TokenizerModel tokenizerModel = new TokenizerModel(this.class.getResourceAsStream("/nlp-models/fr-token.bin"));
        tokenizer = new TokenizerME(tokenizerModel);

        ChunkerModel chunkModel = new ChunkerModel(this.class.getResourceAsStream("/nlp-models/fr-chunk.bin"));
        chunker = new ChunkerME(chunkModel);

        POSModel posModel = new POSModel(this.class.getResourceAsStream("/nlp-models/fr-pos.bin"));
        tagger = new POSTaggerME(posModel);
    }

    def extractNouns(String sentence)
    {
        String[] tokens = tokenizer.tokenize(sentence);
        String[] tags = tagger.tag(tokens);

        println(tokens.join(", "))
        println(tags.join(", "))

        String[] chunks = chunker.chunk(tokens, tags);
        println(chunks.join(","))

        def indexes = tags.findIndexValues({ String tag -> ["NC", "NPP"].contains(tag) });

        println("Indexes : " + indexes.join(" / "))

        def result = indexes.collect({ Long index -> tokens[index] }).unique(false)

        println("RESULT:")
        println(result.join(", "))

        result
    }
}

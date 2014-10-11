package fhacktory.nlp

import spock.lang.Specification

/**
 * @version $Id$
 */
class NounsExtractorSpec extends Specification
{
    def nounExtractor

    def setupSpec() {

    }

    def setup() {
        nounExtractor = new NounsExtractor()
        nounExtractor.init()
    }


    def "Test extract nouns in french"()
    {
        when: "extracting nouns"
        def nouns = nounExtractor.extractNouns(
                "On ne se bat pas pour la meme chose ni avec la meme volonté, chacun sa croix chacun sa cause mais tout les destin sont liées. <3")

        then: "we have some nouns"
        assert 1 == 2

        println nouns
    }
}

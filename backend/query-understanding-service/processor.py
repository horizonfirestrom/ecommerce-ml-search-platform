import spacy
import nltk
from nltk.corpus import wordnet

nltk.download("wordnet")

nlp = spacy.load("en_core_web_sm")

def expand_synonyms(word):
    synonyms = set()
    for syn in wordnet.synsets(word):
        for lemma in syn.lemmas():
            synonyms.add(lemma.name().replace("_", " "))
    return list(synonyms)

def clean_query(text):
    doc = nlp(text.lower())
    tokens = []
    for token in doc:
        if not token.is_stop and token.is_alpha:
            tokens.append(token.lemma_)
    return " ".join(tokens)

def process_query(text):
    cleaned = clean_query(text)
    expanded = []
    for word in cleaned.split():
        expanded.extend(expand_synonyms(word)[:3])
    return {
        "original": text,
        "cleaned": cleaned,
        "expanded_keywords": list(set(expanded)),
    }

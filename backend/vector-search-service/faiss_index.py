import faiss
import numpy as np
import os

EMBED_FILE = "embeddings/vectors.npy"
INDEX_FILE = "index/faiss.index"


def load_index(requested_dim=None):
    """
    Loads or creates FAISS index. 
    If requested_dim is passed, regenerate index for that dimension.
    """

    # If index exists and no new dimension requested → load existing index
    if os.path.exists(INDEX_FILE) and os.path.exists(EMBED_FILE) and requested_dim is None:
        print("🔵 Loading existing FAISS index...")
        index = faiss.read_index(INDEX_FILE)
        ids = np.load(EMBED_FILE)
        return index, ids

    # If dimension is unknown, default to 384
    if requested_dim is None:
        requested_dim = 384

    print(f"🟡 Creating FAISS index with dimension = {requested_dim}...")

    total_products = 100
    ids = np.array([f"product_{i}" for i in range(total_products)])
    vectors = np.random.random((total_products, requested_dim)).astype("float32")

    # Create FAISS index
    index = faiss.IndexFlatL2(requested_dim)
    index.add(vectors)

    os.makedirs("embeddings", exist_ok=True)
    os.makedirs("index", exist_ok=True)

    np.save(EMBED_FILE, ids)
    faiss.write_index(index, INDEX_FILE)

    print("🟢 Dummy FAISS index created.")

    return index, ids

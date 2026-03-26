
# Gold Set

This directory contains the actual key classes for each subject system.

The key classes used as ground truth in our experiments are not constructed by us using structural metrics (e.g., degree, coupling, or centrality), nor are they derived from any automated labeling algorithm that could introduce circularity. Instead, these ground truths are borrowed directly from established literature in the field of key class identification.

In these foundational studies, key classes were identified based on explicit design documentation and developer descriptions. Specifically, original developers or architects designated these classes as core, architectural overview, or critical components through:

- Free-text descriptions in design documents.
- Pruned architectural diagrams highlighting core modules.
- Official project documentation stating the system’s backbone.

Therefore, our ground truth reflects the semantic intent of the original developers, which is orthogonal to the structural topology analyzed by BiMo. Since the labels are derived from human design intent and our method analyzes code structure, there is no circular dependency where structure predicts structure.

It is important to distinguish user manuals from the design documentation used to establish the ground truth. While the user manuals (used for computing BE) focus on functional usage and operational workflows, the ground truth labels were derived exclusively from architectural overviews and design specifications that identify core components. This separation ensures that our evaluation measures the approach’s ability to identify key classes from functional descriptions and software networks, rather than simply matching explicit design statements.

Since the ground truth classes are factually defined by the original system architects in official documentation (rather than being subjectively labeled by multiple independent annotators in our study), the concept of inter-annotator agreement does not apply in the traditional sense. The consistency is guaranteed by the authoritative nature of the design documents themselves. These datasets are widely accepted benchmarks in the software engineering community precisely because they represent the ground truth of developer intent, not a consensus of subjective opinions.
